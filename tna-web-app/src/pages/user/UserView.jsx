import React, { Component } from "react";

import withStyles from "@material-ui/styles/withStyles";
import Paper from "@material-ui/core/Paper";

import { PageHeader, SimpleShowLayout, TextField } from "jazasoft";

import UserController from "./UserController";

const fieldOptions = sm => ({
  xs: 12,
  sm
});

const styles = theme => ({
  container: {
    margin: theme.spacing(3),
    padding: theme.spacing(3)
  }
});

class UserView extends Component {
  render() {
    const { classes, user, authUser = {}, basePath, resource, i18nKey } = this.props;

    const record = {
      ...user,
      ...authUser,
      roleList: authUser && authUser.roleList ? authUser.roleList.map(r => r.role && r.role.name).join(", ") : "",
      department: authUser && authUser.permissionList ? authUser.permissionList.filter(p => p.key === "departmentId").map(p => p.name)[0] : "",
      team: authUser && authUser.permissionList ? authUser.permissionList.filter(p => p.key === "teamId").map(p => p.name)[0] : "",
      buyers:
        authUser && authUser.permissionList
          ? authUser.permissionList
              .filter(p => p.key === "buyerId")
              .map(p => p.name)
              .join(", ")
          : ""
    };
    return (
      <div>
        <PageHeader title="View User" />

        <Paper className={classes.container}>
          <SimpleShowLayout record={record} basePath={basePath} resource={resource} i18nKey={i18nKey}>
            <TextField source="fullName" {...fieldOptions(3)} />
            <TextField source="username" {...fieldOptions(3)} />
            <TextField source="email" {...fieldOptions(3)} />
            <TextField source="mobile" {...fieldOptions(3)} />
            <TextField label="Roles" source="roleList" {...fieldOptions(3)} />
            <TextField label="Department" source="department" {...fieldOptions(3)} />
            <TextField label="Team" source="team" {...fieldOptions(3)} />
            <TextField label="Buyer Access" source="buyers" {...fieldOptions(3)} />
          </SimpleShowLayout>
        </Paper>
      </div>
    );
  }
}

const StyledUserView = withStyles(styles)(UserView);

export default props => <UserController {...props}>{controllerProps => <StyledUserView {...props} {...controllerProps} />}</UserController>;
