import React from "react";
import { withStyles } from "@material-ui/core/styles";
import {
  Create,
  Edit,
  List,
  SimpleForm,
  TextInput,
  TextField,
  required,
  minLength,
  Datagrid,
  CreateButton,
  EditButton,
  DeleteButton
} from "jazasoft";
import hasPrivilege from "../../utils/hasPrivilege";
import Forbidden from "../../components/Forbidden";

const homeStyle = theme => ({
  buttonEdit: {
    width: theme.spacing(14)
  },
  buttonDelete: {
    width: theme.spacing(16)
  }
});

export const EditDepartment = ({ classes, ...props }) => {
  if (!hasPrivilege(props.roles, props.hasAccess, "buyer", "update")) {
    return <Forbidden history={props.history} />;
  }
  return (
    <Edit {...props}>
      <SimpleForm redirect="home" form="record-form-edit">
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Edit>
  );
};

export const CreateDepartment = ({ classes, ...props }) => {
  if (!hasPrivilege(props.roles, props.hasAccess, "department", "write")) {
    return <Forbidden history={props.history} />;
  }

  return (
    <Create {...props}>
      <SimpleForm redirect="home">
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Create>
  );
};

export const DepartmentHome = withStyles(homeStyle)(({ classes, ...props }) => {
  const { roles, hasAccess } = props;
  return (
    <List
      actions={({ basePath, roles, hasAccess }) => (
        <div>{hasPrivilege(roles, hasAccess, "department", "write") && <CreateButton basePath={basePath} showLabe={false} />}</div>
      )}
      {...props}
    >
      <Datagrid>
        <TextField source="name" />
        <TextField source="desc" />
        {hasPrivilege(roles, hasAccess, "department", "update") && <EditButton cellClassName={classes.buttonEdit} />}
        {hasPrivilege(roles, hasAccess, "department", "delete") && <DeleteButton cellClassName={classes.buttonDelete} />}
      </Datagrid>
    </List>
  );
});
