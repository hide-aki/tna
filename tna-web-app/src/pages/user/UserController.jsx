import { Component } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import compose from "recompose/compose";
import inflection from "inflection";

import { crudGetOne, translate, RestMethods, FETCH_START, FETCH_END, SAVING_START, SAVING_END, showNotification } from "jazasoft";

import { authDataProvider, dataProvider, appId } from "../../App";
import handleError from "../../utils/handleError";

export class UserController extends Component {
  state = {
    app: {},
    authUser: null,
    roleList: [],
    employeeIds: []
  };

  componentDidMount() {
    this.fetchUser();
    this.fetchRoles();
    this.fetchPermissions();
    const app = this.props.authState && this.props.authState.appList && this.props.authState.appList.find(app => app.appId === appId);
    this.setState({ app });
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.id !== nextProps.id || nextProps.version !== this.props.version) {
      this.fetchUser(nextProps.resource, nextProps.id);
    }
  }

  fetchUser = (resource = this.props.resource, id = this.props.id) => {
    if (id) {
      this.props.dispatch(crudGetOne(resource, id, this.props.basePath, this.props.requestConfig));

      //Fetch Auth Server User
      const options = {
        tenantId: this.props.authState && this.props.authState.clientId,
        url: `users/${id}`,
        method: "get"
      };
      this.props.dispatch({ type: FETCH_START });

      authDataProvider(RestMethods.CUSTOM, undefined, options)
        .then(response => {
          if (response.status === 200) {
            this.setState({ authUser: response.data });
          }
          this.props.dispatch({ type: FETCH_END });
        })
        .catch(err => {
          handleError(err, this.props.dispatch);
          this.props.dispatch({ type: FETCH_END });
        });
    }
  };

  fetchRoles = () => {
    const options = {
      tenantId: this.props.authState && this.props.authState.clientId,
      url: `roles`,
      method: "get"
    };
    this.props.dispatch({ type: FETCH_START });

    authDataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        if (response.status === 200) {
          const roleList = response.data && response.data.content;
          console.log({ roleList });
          this.setState({ roleList });
        }
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
      });
  };

  fetchPermissions = () => {
    const options = {
      tenantId: this.props.authState && this.props.authState.clientId,
      url: `permissions`,
      method: "get",
      params: { search: `key=in=('buyerId', 'teamId', 'departmentId')` }
    };
    this.props.dispatch({ type: FETCH_START });

    authDataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        if (response.status === 200) {
          const permissionList = response.data && response.data.content;
          console.log({ permissionList });
          this.setState({ permissionList });
        }
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
      });
  };

  saveUser = async ({ roleIds, departmentId, teamId, buyerIds, ...user }) => {
    const { authState = {} } = this.props;
    // First save data in Auth Server
    let options = {
      tenantId: this.props.authState && this.props.authState.clientId,
      url: `users`,
      method: "post",
      data: {
        ...user,
        tenantId: authState.tenant && authState.tenant.id,
        apps: [{ id: this.state.app.id, roleIds, departmentId, teamId, buyerIds }]
      }
    };

    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });

    try {
      let response = await authDataProvider(RestMethods.CUSTOM, undefined, options);
      let authUser = response.data;

      // Then Save data in Application
      options.data = {
        ...user,
        ...authUser,
        roles: authUser.roleList && authUser.roleList.map(r => r.role && r.role.roleId).join(",")
      };
      response = await dataProvider(RestMethods.CUSTOM, undefined, options);
      if (response.status === 201 || response.status === 200) {
        this.props.dispatch(showNotification(`User saved successfully.`));
        this.props.history.push("/users");
      }

      this.props.dispatch({ type: FETCH_END });
      this.props.dispatch({ type: SAVING_END });
    } catch (err) {
      handleError(err, this.props.dispatch);
      this.props.dispatch({ type: FETCH_END });
      this.props.dispatch({ type: SAVING_END });
    }
  };

  updateUser = ({ authorities, roleIds, departmentId, teamId, buyerIds, ...user }) => {
    const authState = this.props.authState || {};
    let options = {
      tenantId: this.props.authState && this.props.authState.clientId,
      url: `users/${user.id}`,
      method: "put",
      data: {
        ...user,
        tenantId: authState.tenant && authState.tenant.id,
        apps: [{ id: this.state.app.id, roleIds, departmentId, teamId, buyerIds }]
      }
    };

    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    authDataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        if (response.status === 201 || response.status === 200) {
          let authUser = response.data;

          options.data = {
            ...user,
            ...authUser,
            name: `${authUser.firstName} ${authUser.lastName}`,
            roles: authUser.roleList && authUser.roleList.map(r => r.role && r.role.roleId).join(",")
          };

          dataProvider(RestMethods.CUSTOM, undefined, options).then(response => {
            if (response.status === 201 || response.status === 200) {
              this.props.dispatch(showNotification(`User updated successfully.`));
              this.props.history.push("/users");
            }
            this.props.dispatch({ type: FETCH_END });
            this.props.dispatch({ type: SAVING_END });
          });
        }
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  render() {
    const { id, basePath, resource, i18nKey, isLoading, saving, format, record, authState, children, dispatch, version, translate } = this.props;

    if (!children) return null;

    const name = translate(`resources.${i18nKey || resource}.name`, {
      smart_count: 1,
      _: inflection.humanize(inflection.singularize(i18nKey || resource))
    });
    const defaultTitle = translate("js.page.show", {
      name: `${name}`,
      id
    });

    return children({
      id,
      isLoading,
      saving,
      defaultTitle,
      resource,
      basePath,
      user: format(record),
      authState,
      translate,
      version,
      dispatch,
      saveUser: this.saveUser,
      updateUser: this.updateUser,
      ...this.state
    });
  }
}

UserController.propTypes = {
  //props provided by user
  className: PropTypes.string,
  classes: PropTypes.object,
  children: PropTypes.func.isRequired,
  requestConfig: PropTypes.object,
  format: PropTypes.func,
  //props from state
  record: PropTypes.object,
  isLoading: PropTypes.bool,
  translate: PropTypes.func.isRequired,
  // from framework
  basePath: PropTypes.string,
  resource: PropTypes.string,
  referenceResource: PropTypes.string,
  i18nKey: PropTypes.string
};

UserController.defaultProps = {
  requestConfig: {},
  format: record => record
};

const mapStateToProps = (state, props) => {
  const resourceState = state.jazasoft.resources[props.resource];
  return {
    id: props.id,
    authState: state.jazasoft.auth,
    record: resourceState.data[props.id],
    isLoading: state.jazasoft.loading > 0,
    saving: state.jazasoft.saving,
    version: state.jazasoft.ui.viewVersion
  };
};

export default compose(translate, connect(mapStateToProps))(UserController);
