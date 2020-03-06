package com.jazasoft.tna.service;

import com.jazasoft.mtdb.util.MtdbUtils;
import com.jazasoft.mtdb.util.RestUtils;

import com.jazasoft.tna.ConfigKeys;
import com.jazasoft.tna.Constants;
import com.jazasoft.tna.dto.Api;
import com.jazasoft.tna.dto.ExcelRowError;
import com.jazasoft.tna.entity.User;
import com.jazasoft.tna.exception.ExcelUploadNotValidException;
import com.jazasoft.tna.repository.UserRepository;
import com.jazasoft.tna.util.MapBuilder;
import com.jazasoft.util.StringUtils;
import com.jazasoft.util.Utils;
import com.jazasoft.util.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class UserService {
  private final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final RestUtils restUtils;

  public UserService(UserRepository userRepository, RestUtils restUtils) {
    this.userRepository = userRepository;
    this.restUtils = restUtils;
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public List<User> findAll(Specification<User> spec) {
    return userRepository.findAll(spec);
  }

  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  public Page<User> findAll(Specification<User> spec, Pageable pageable) {
    return userRepository.findAll(spec, pageable);
  }

  public User findOne(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id = " + id + " not found."));
  }

  @Transactional(value = "tenantTransactionManager")
  public User save(User user) {
    return userRepository.save(user);
  }

  @Transactional(value = "tenantTransactionManager")
  public List<User> saveBatch(List<User> userList) {
    validateUsers(userList);
    userList = userRepository.saveAll(userList);
    return userList;
  }

  @Transactional(value = "tenantTransactionManager")
  public User update(User user) {
    User mUser = userRepository.findById(user.getId()).orElseThrow();

    mUser.setId(user.getId());
    mUser.setFullName(user.getFullName());
    mUser.setUsername(user.getUsername());
    mUser.setEmail(user.getEmail());
    mUser.setMobile(user.getMobile());
    mUser.setRoles(user.getRoles());
    mUser.setDepartmentId(user.getDepartmentId());
    mUser.setTeamId(user.getTeamId());
    mUser.setBuyerIds(user.getBuyerIds());
    return mUser;
  }

//  @Transactional
//  public void registerPushToken(Long userId, String pushToken) {
//    logger.debug("userId = {}, pushToken = {}", userId, pushToken);
//    User mUser = userRepository.findById(userId).orElseThrow();
//    mUser.setPushToken(pushToken);
//  }

  @Transactional(value = "tenantTransactionManager")
  public void delete(Long id) {
    userRepository.deleteById(id);
  }

  public boolean exists(Long id) {
    return userRepository.existsById(id);
  }

  @SuppressWarnings("unchecked")
  @Transactional(value = "tenantTransactionManager")
  public void syncUsers(String tenantId) {
    try {
      String appId = MtdbUtils.getAppId();
      String confFile = MtdbUtils.getAppHome() + File.separator + "conf" + File.separator + tenantId + ".yml";
      Map<String, Object> iamMap = (Map<String, Object>) YamlUtils.getInstance().getProperty(confFile, ConfigKeys.IAM);
      if (iamMap == null) {
        logger.error("'iam' configuration missing in config.yml file");
        return;
      }
      String baseUrl = (String) iamMap.get("endpoint");
      List<Api> apiList = (List<Api>) iamMap.get("apis");
      if (baseUrl == null || apiList == null) {
        logger.error("'iam' configuration missing in config.yml file");
        return;
      }
      Api api = apiList.stream().filter(a -> tenantId.equals(a.getTenantId()) && "user".equals(a.getName())).findAny().orElse(null);
      if (api == null) {
        logger.error("Api entry for api = user, tenantId = {} is missing", tenantId);
        return;
      }
      if (api.getEndpoint() == null || api.getApiKey() == null) {
        logger.error("Api endpoint and Api Key is required for making api call. api = user, tenantId = {}", tenantId);
        return;
      }
      long lastSync = api.getLastSync() != null ? api.getLastSync() : 0L;


      String url = baseUrl + api.getEndpoint();
      Map<String, String> headers = new MapBuilder<String, String>().put(Constants.API_KEY_HEADER, api.getApiKey()).build();
      Map<String, String> params = new MapBuilder<String, String>().put("search", "modifiedAt=ge=" + lastSync).build();

      Long currentTime = System.currentTimeMillis();

      Map resp = restUtils.get(url, params, headers, Map.class);
      if (resp != null) {
        List<Map> users = (List<Map>) resp.getOrDefault("content", new ArrayList<>());
        logger.debug("updated user count = {}", users.size());
        List<User> userList = users.stream().map(map -> userFromMap(map, tenantId, appId)).collect(Collectors.toList());


        for (User user : userList) {
          if (exists(user.getId())) {
            update(user);
          } else {
            save(user);
          }
        }

        api.setLastSync(currentTime);

        // update last sync time
        Map root = (Map) YamlUtils.getInstance().getProperty(new File(confFile));
        if (root != null) {
          root.put(ConfigKeys.IAM, iamMap);
          YamlUtils.getInstance().writeProperties(confFile, root);
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  private User userFromMap(Map userMap, String tenantId, String appId) {
    if (userMap == null) return null;
    User user = new User();
    user.setId(Long.parseLong(userMap.get("id").toString()));
    user.setFullName((String) userMap.get("fullName"));
    user.setUsername((String) userMap.get("username"));
    user.setEmail((String) userMap.get("email"));
    user.setMobile((String) userMap.get("mobile"));
    if (userMap.containsKey("roleList")) {
      List<Map> roleList = (List<Map>) userMap.get("roleList");
      Set<String> roles = roleList.stream().map(m -> (String) ((Map) m.getOrDefault("role", new HashMap<>())).getOrDefault("roleId", "")).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
      user.setRoles(Utils.getCsvFromIterable(roles));
    }
    if (userMap.containsKey("permissionList")) {
      List<Map> permissionList = (List<Map>) userMap.get("permissionList");

      List<Long> departmentIdList = permissionList.stream()
          .filter(pMap -> {
            String key = (String) pMap.get("key");
            String value = (String) pMap.get("value");
            if (value == null || !"departmentId".equals(key)) return false;
            Map app = (Map) pMap.get("app");
            Map tenant = (Map) pMap.get("tenant");
            if (app != null && tenant != null) {
              String aId = (String) app.get("appId");
              String tId = (String) tenant.get("tenantId");
              if (appId.equals(aId) && tenantId.equals(tId)) return true;
            }
            return false;
          })
          .map(pMap -> (String) pMap.get("value"))
          .map(Long::parseLong)
          .collect(Collectors.toList());
      if (departmentIdList.size() > 1) {
        logger.error("Multiple department for User [id = {}, username = {}, roles = {}], departmentIds = {}", user.getId(), user.getUsername(), user.getRoles(), departmentIdList);
      } else if (departmentIdList.size() == 1) {
        user.setDepartmentId(departmentIdList.get(0));
      } else {
        logger.warn("No department for User [id = {}, username = {}, roles = {}]", user.getId(), user.getUsername(), user.getRoles());
      }
      List<Long> teamIdList = permissionList.stream()
          .filter(pMap -> {
            String key = (String) pMap.get("key");
            String value = (String) pMap.get("value");
            if (value == null || !"teamId".equals(key)) return false;
            Map app = (Map) pMap.get("app");
            Map tenant = (Map) pMap.get("tenant");
            if (app != null && tenant != null) {
              String aId = (String) app.get("appId");
              String tId = (String) tenant.get("tenantId");
              if (appId.equals(aId) && tenantId.equals(tId)) return true;
            }
            return false;
          })
          .map(pMap -> (String) pMap.get("value"))
          .map(Long::parseLong)
          .collect(Collectors.toList());
      if (teamIdList.size() > 1) {
        logger.error("Multiple team for User [id = {}, username = {}, roles = {}], teamIds = {}", user.getId(), user.getUsername(), user.getRoles(), teamIdList);
      } else if (departmentIdList.size() == 1) {
        user.setTeamId(teamIdList.get(0));
      }

      Set<String> buyerIdList = permissionList.stream()
          .filter(pMap -> {
            String key = (String) pMap.get("key");
            String value = (String) pMap.get("value");
            if (value == null || !"buyerId".equals(key)) return false;
            Map app = (Map) pMap.get("app");
            Map tenant = (Map) pMap.get("tenant");
            if (app != null && tenant != null) {
              String aId = (String) app.get("appId");
              String tId = (String) tenant.get("tenantId");
              if (appId.equals(aId) && tenantId.equals(tId)) return true;
            }
            return false;
          })
          .map(pMap -> (String) pMap.get("value"))
          .collect(Collectors.toSet());
      user.setBuyerIds(Utils.getCsvFromIterable(buyerIdList));
    }
    return user;
  }


  private void validateUsers(List<User> userList) {
    List<ExcelRowError> errorList = new ArrayList<>();

    int idx = 1;
    for (User user : userList) {
      if (!StringUtils.hasText(user.getFullName())) {
        errorList.add(new ExcelRowError("fullName", String.valueOf(idx), "Full Name Required."));
      }

      if (!StringUtils.hasText(user.getUsername())) {
        errorList.add(new ExcelRowError("username", String.valueOf(idx), "Username Required."));
      }

      if (!StringUtils.hasText(user.getMobile())) {
        errorList.add(new ExcelRowError("mobile", String.valueOf(idx), "Mobile Required."));
      }

      if (!StringUtils.hasText(user.getRoles())) {
        errorList.add(new ExcelRowError("roles", String.valueOf(idx), "Roles Required."));
      }

    }

    if (!errorList.isEmpty()) {
      throw new ExcelUploadNotValidException("User List has errors", errorList);
    }
  }
}
