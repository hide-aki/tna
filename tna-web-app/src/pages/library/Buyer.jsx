import React from "react";
import { withStyles } from "@material-ui/styles";
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

const createStyle = theme => ({});

export const CreateBuyer = withStyles(createStyle)(({ classes, ...props }) => {
  if (!hasPrivilege(props.roles, props.hasAccess, "buyer", "write")) {
    return <Forbidden history={props.history} />;
  }
  return (
    <Create {...props}>
      <SimpleForm className={classes.form} redirect="home">
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Create>
  );
});

const editStyle = theme => ({});

export const EditBuyer = withStyles(editStyle)(({ classes, ...props }) => {
  if (!hasPrivilege(props.roles, props.hasAccess, "buyer", "update")) {
    return <Forbidden history={props.history} />;
  }
  return (
    <Edit {...props}>
      <SimpleForm className={classes.form} redirect="home" form="record-form-edit">
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Edit>
  );
});

const homeStyle = theme => ({
  buttonEdit: {
    width: theme.spacing(14)
  },
  buttonDelete: {
    width: theme.spacing(16)
  }
});

export const BuyerHome = withStyles(homeStyle)(({ classes, ...props }) => {
  const { roles, hasAccess } = props;
  return (
    <List
      actions={({ basePath, roles, hasAccess }) => (
        <div>
          {hasPrivilege(roles, hasAccess, "buyer", "write") && <CreateButton basePath={basePath} showLabel={false} />}
        </div>
      )}
      {...props}
    >
      <Datagrid>
        <TextField source="name" />
        <TextField source="desc" />
        {hasPrivilege(roles, hasAccess, "buyer", "update") && <EditButton cellClassName={classes.buttonEdit} />}
        {hasPrivilege(roles, hasAccess, "buyer", "delete") && <DeleteButton cellClassName={classes.buttonDelete} />}
      </Datagrid>
    </List>
  );
});
