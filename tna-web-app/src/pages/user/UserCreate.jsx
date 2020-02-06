import React, { Component } from "react";

import withStyles from "@material-ui/styles/withStyles";
import Paper from "@material-ui/core/Paper";

import {
  PageHeader,
  SimpleForm,
  TextInput,
  FormDataConsumer,
  SelectInput,
  AutoCompleteArrayInput,
  SelectArrayInput,
  required,
  minLength,
  regex
} from "jazasoft";

import UserController from "./UserController";

import { REGEX_USERNAME, REGEX_EMAIL, REGEX_MOBILE } from "../../utils/regex";

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

class UserCreate extends Component {
  onSubmit = values => {
    this.props.saveUser(values);
  };

  render() {
    const { classes, roleList, permissionList } = this.props;

    const choicesRoles = roleList ? roleList.map(r => ({ id: r.id, name: r.name })) : [];
    const choicesDepartments = permissionList ? permissionList.filter(p => p.key === "departmentId").map(r => ({ id: r.id, name: r.name })) : [];
    const choicesBuyers = permissionList ? permissionList.filter(p => p.key === "buyerId").map(r => ({ id: r.id, name: r.name })) : [];

    return (
      <div>
        <PageHeader title="Create User" />

        <Paper className={classes.container}>
          <SimpleForm onSubmit={this.onSubmit}>
            <TextInput source="fullName" validate={[required(), minLength(2)]} {...inputOptions(3)} />
            <TextInput
              source="username"
              validate={[
                required(),
                regex(
                  REGEX_USERNAME,
                  "Username should consists of Alphabets, numbers and (hyphen, underscore, dot) special characters only and starting with Alphabet"
                )
              ]}
              {...inputOptions(3)}
            />
            <TextInput source="email" validate={[regex(REGEX_EMAIL, "Invalid Email")]} {...inputOptions(3)} />
            <TextInput
              source="mobile"
              validate={[required(), regex(REGEX_MOBILE, "Mobile number must be 10 digit numeric value")]}
              {...inputOptions(3)}
            />
            <SelectArrayInput label="Roles" validate={[required()]} source="roleIds" choices={choicesRoles} {...inputOptions(3)} />
            <SelectInput label="Department" validate={[required()]} source="departmentId" choices={choicesDepartments} {...inputOptions(3)} />
            <FormDataConsumer {...inputOptions(3)}>
              {({ formData }) => {
                const department = formData.departmentId && permissionList.find(p => p.id === formData.departmentId);
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

const StyledUserCreate = withStyles(styles)(UserCreate);

export default props => <UserController {...props}>{controllerProps => <StyledUserCreate {...props} {...controllerProps} />}</UserController>;
