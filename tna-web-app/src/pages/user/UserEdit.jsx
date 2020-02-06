import React, { Component } from "react";

import withStyles from "@material-ui/styles/withStyles";

import { PageHeader, SimpleForm, TextInput, SelectInput, FormDataConsumer, AutoCompleteArrayInput, SelectArrayInput, required, minLength } from "jazasoft";

import UserController from "./UserController";
import { Paper } from "@material-ui/core";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const styles = theme => ({
  container: {
    margin: theme.spacing(3),
    padding: theme.spacing(3)
  }
});

class UserEdit extends Component {
  onSubmit = values => {
    this.props.updateUser(values);
  };

  render() {
    const { classes, roleList, permissionList, user, authUser, app } = this.props;

    const choicesRoles = roleList ? roleList.map(r => ({ id: r.id, name: r.name })) : [];
    const choicesDepartments = permissionList ? permissionList.filter(p => p.key === "departmentId").map(r => ({ id: r.id, name: r.name })) : [];
    const choicesBuyers = permissionList ? permissionList.filter(p => p.key === "buyerId").map(r => ({ id: r.id, name: r.name })) : [];

    const initialValues = {
      ...user,
      ...authUser,
      roleIds: authUser && authUser.roleList && authUser.roleList.filter(r => r.appId === app.id).map(r => r.role && r.role.id),
      departmentId: authUser && authUser.permissionList.filter(p => p.app && p.app.id === app.id && p.key === "departmentId").map(p => p.id)[0],
      teamId: authUser && authUser.permissionList.filter(p => p.app && p.app.id === app.id && p.key === "teamId").map(p => p.id)[0],
      buyerIds: authUser && authUser.permissionList.filter(p => p.app && p.app.id === app.id && p.key === "buyerId").map(p => p.id)
    };

    return (
      <div>
        <PageHeader title="Edit User" />

        <Paper className={classes.container}>
          <SimpleForm record={initialValues} onSubmit={this.onSubmit}>
            <TextInput source="fullName" validate={[required(), minLength(2)]} {...inputOptions(3)} />
            <TextInput source="username" validate={[required()]} {...inputOptions(3)} />
            <TextInput source="email" {...inputOptions(3)} />
            <TextInput source="mobile" validate={[required()]} {...inputOptions(3)} />
            <SelectArrayInput label="Roles" source="roleIds" validate={[required()]} choices={choicesRoles} {...inputOptions(3)} />
            <SelectInput label="Department" validate={[required()]} source="departmentId" choices={choicesDepartments} {...inputOptions(3)} />
            <FormDataConsumer {...inputOptions(3)}>
              {({ formData }) => {
                const department = formData.departmentId && permissionList && permissionList.find(p => p.id === formData.departmentId);
                let choicesTeams = [];
                if (department) {
                  choicesTeams = permissionList
                    ? permissionList.filter(p => p.key === "teamId" && p.parentValue === department.value).map(r => ({ id: r.id, name: r.name }))
                    : [];
                }
                return <SelectInput label="Team" validate={[required()]} source="teamId" choices={choicesTeams} {...inputOptions(3)} />;
              }}
            </FormDataConsumer>
            <AutoCompleteArrayInput label="Buyers" validate={[required()]} source="buyerIds" choices={choicesBuyers} {...inputOptions(3)} />
          </SimpleForm>
        </Paper>
      </div>
    );
  }
}

const StyledUserEdit = withStyles(styles)(UserEdit);

export default props => <UserController {...props}>{controllerProps => <StyledUserEdit {...props} {...controllerProps} />}</UserController>;
