import * as React from "react";
import { createBrowserHistory } from "history";
import axios from "axios";
import { Route } from "react-router-dom";

import logo from "./asset/img/logo2.png";
import avatar from "./asset/img/faces/avatar-male.png";
//icons
import MenuIcon from "@material-ui/icons/Menu";
import CalendarIcon from "mdi-material-ui/CalendarMonth";
import OrderIcon from "@material-ui/icons/FiberManualRecordOutlined";
import UserIcon from "mdi-material-ui/AccountGroup";
import LibraryIcon from "@material-ui/icons/LibraryBooks";
import SettingsIcon from "@material-ui/icons/Settings";
import HelpIcon from "mdi-material-ui/Lifebuoy";
import ManualIcon from "mdi-material-ui/FileDocument";
// import DeveloperBoardIcon from "@material-ui/icons/DeveloperBoard";
import DownloadsIcon from "@material-ui/icons/GetApp";
import HourglassEmptyOutlinedIcon from "@material-ui/icons/HourglassEmptyOutlined";
// import TicketIcon from "@material-ui/icons/ConfirmationNumber";
import ActivityIcon from "mdi-material-ui/ClipboardList";

import { App as JApp, Resource, createAuthProvider, createDataProvider, WithPermissions } from "jazasoft";

// import Dashboard from "./pages/dashboard/Dashboard";
import Calendar from "./pages/calender/Calendar";

import englishMessage from "./i18n/en";
import theme from "./theme";

import hasPrivilege from "./utils/hasPrivilege";
import { Role } from "./utils/types";

import { OrderHome, OrderCreate, OrderEdit, OrderView } from "./pages/order";
import { TimelineHome, TimelineCreate, TimelineView, TimelineEdit } from "./pages/timeline";
import { ActivityHome, ActivityCreate, ActivityView, ActivityEdit } from "./pages/activity";
import { UserHome, UserCreate, UserEdit, UserView, UserUpload } from "./pages/user";
// Library Pages
import { BuyerHome, CreateBuyer, EditBuyer } from "./pages/library/Buyer";
import { GarmentTypeHome, CreateGarmentType, EditGarmentType } from "./pages/library/GarmentType";
import { SeasonHome, CreateSeason, EditSeason } from "./pages/library/Season";
import { DepartmentHome, CreateDepartment, EditDepartment } from "./pages/library/Department";
import { TeamHome, CreateTeam, EditTeam } from "./pages/library/Team";
import Profile from "./pages/profile/Profile";

// Setting Page
import Settings from "./pages/setting/Settings";
import Downloads from "./pages/downloads/Downloads";

import { Docs, Section, Topic, CreateTopic, EditTopic } from "./pages/docs";

export const appId = "tna";

//const rootUrl = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ":" + window.location.port : "");
const rootUrl = `http://${window.location.hostname}:8006`;
// const rootUrl = `https://tna.jaza-soft.com`;

///// Env: Local IAM ///////
// const authProvider = createAuthProvider(`http://${window.location.hostname}:8081`, "Basic Y2xpZW50OnNlY3JldA==", appId);
// export const authDataProvider = createDataProvider(`http://${window.location.hostname}:8081/api`, appId);

/// Env: Production ///////
const authProvider = createAuthProvider(
  tenantId => (tenantId ? `https://iam-${tenantId}.jaza-soft.com` : "https://iam.jaza-soft.com"),
  "Basic Y2xpZW50OnNlY3JldA==",
  appId
);
export const authDataProvider = createDataProvider(
  tenantId => (tenantId ? `https://iam-${tenantId}.jaza-soft.com/api` : "https://iam.jaza-soft.com/api"),
  appId
);
export const dataProvider = createDataProvider(`${rootUrl}/api`, appId);

(function() {
  axios
    .get(`${rootUrl}/buildInfo`)
    .then(response => {
      localStorage.setItem(`${appId}-build-info`, JSON.stringify(response.data));
    })
    .catch(err => {
      console.log(err);
    });
})();

const i18nProvider = locale => {
  return englishMessage;
};

const history = createBrowserHistory();

const initialState = {
  jazasoft: {
    ui: {
      sidebarOpen: false,
      filterOpen: false,
      optimistic: false,
      viewVersion: 0
    }
  }
};

const resources = [
  // { i18nKey: "orders", resource: "orders" },
  // { i18nKey: "users", resource: "users" },
  { i18nKey: "buyers", resource: "buyers" }
];

const customRoutes = [
  <Route name="profile" exact path="/profile" component={Profile} />,

  <Route name="users" resource="users" exact path="/users/upload" component={UserUpload} />,
  ...["help", "manual"].flatMap(type => [
    <Route
      name={type}
      exact
      path={`/${type}/:sectionId`}
      render={routeProps => <WithPermissions render={props => React.createElement(Section, { ...props, type })} {...routeProps} />}
    />,
    <Route
      name={type}
      exact
      path={`/${type}/topics/create`}
      render={routeProps => <WithPermissions render={props => React.createElement(CreateTopic, { ...props, type })} {...routeProps} />}
    />,
    <Route
      name={type}
      exact
      path={`/${type}/topics/:id/edit`}
      render={routeProps => <WithPermissions render={props => React.createElement(EditTopic, { ...props, type })} {...routeProps} />}
    />,
    <Route
      name={type}
      exact
      path={`/${type}/topics/:id/view`}
      render={routeProps => <WithPermissions render={props => React.createElement(Topic, { ...props, type })} {...routeProps} />}
    />
  ])
];

class App extends React.Component {
  render() {
    return (
      <JApp
        appId={appId}
        appName="Time and Action Calendar"
        appNameShort="TNA"
        authProvider={authProvider}
        dataProvider={dataProvider}
        i18nProvider={i18nProvider}
        resources={resources}
        initialState={initialState}
        customRoutes={customRoutes}
        logo={logo}
        avatar={avatar}
        dashboard={() => {}}
        history={history}
        theme={theme}
      >
        {({ roles, hasAccess }) => {
          let resourceList = [];

          if (roles && (roles.includes(Role.USER) || roles.includes(Role.MERCHANT))) {
            resourceList.push(<Resource name="calendar" home={Calendar} icon={CalendarIcon} />);
          }

          if (hasPrivilege(roles, hasAccess, "order", "read")) {
            resourceList.push(
              <Resource name="orders" resource="orders" home={OrderHome} create={OrderCreate} edit={OrderEdit} view={OrderView} icon={OrderIcon} />
            );
          }
          if (hasPrivilege(roles, hasAccess, "timeline", "read")) {
            resourceList.push(
              <Resource
                name="timelines"
                resource="timelines"
                home={TimelineHome}
                create={TimelineCreate}
                view={TimelineView}
                edit={TimelineEdit}
                icon={HourglassEmptyOutlinedIcon}
              />
            );
          }
          if (hasPrivilege(roles, hasAccess, "activity", "read")) {
            resourceList.push(
              <Resource
                name="activities"
                resource="activities"
                home={ActivityHome}
                create={ActivityCreate}
                view={ActivityView}
                edit={ActivityEdit}
                icon={ActivityIcon}
              />
            );
          }
          if (hasPrivilege(roles, hasAccess)) {
            resourceList.push(
              <Resource name="users" resource="users" home={UserHome} create={UserCreate} edit={UserEdit} view={UserView} icon={UserIcon} />
            );
          }

          if (hasPrivilege(roles, hasAccess, "library", "read")) {
            resourceList.push(
              <Resource name="library" icon={LibraryIcon}>
                <Resource name="buyers" resource="buyers" home={BuyerHome} create={CreateBuyer} edit={EditBuyer} icon={MenuIcon} />
                <Resource
                  name="garmentTypes"
                  resource="garmentTypes"
                  home={GarmentTypeHome}
                  create={CreateGarmentType}
                  edit={EditGarmentType}
                  icon={MenuIcon}
                />
                <Resource name="seasons" resource="seasons" home={SeasonHome} create={CreateSeason} edit={EditSeason} icon={MenuIcon} />
                <Resource
                  name="departments"
                  resource="departments"
                  home={DepartmentHome}
                  create={CreateDepartment}
                  edit={EditDepartment}
                  icon={MenuIcon}
                />
                <Resource name="teams" resource="teams" home={TeamHome} create={CreateTeam} edit={EditTeam} />
              </Resource>
            );
          }

          if (hasPrivilege(roles, hasAccess, "setting", "read")) {
            resourceList.push(<Resource name="settings" home={Settings} icon={SettingsIcon} />);
          }

          resourceList.push(<Resource name="downloads" home={Downloads} icon={DownloadsIcon} />);

          resourceList.push(<Resource name="help" home={Docs} icon={HelpIcon} />);
          if (roles && roles.includes(Role.MASTER)) {
            resourceList.push(<Resource name="manual" home={Docs} icon={ManualIcon} />);
          }

          return resourceList;
        }}
      </JApp>
    );
  }
}

export default App;
