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

export const CreateSeason = ({ ...props }) => {
  if(!hasPrivilege(props.roles, props.hasAccess, "season", "write")){
    return <Forbidden history={props.history} />
  }
  return (
    <Create {...props}>
      <SimpleForm redirect="home" >
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Create>
  );
};

export const EditSeason = ({ ...props }) => {
  if(!hasPrivilege(props.roles, props.hasAccess, "season", "update")){
    return <Forbidden history={props.history} />
  }
  return (
    <Edit {...props}>
      <SimpleForm>
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Edit>
  );
};

const homeStyle = theme => ({
  buttonEdit: {
    width: theme.spacing(14)
  },
  buttonDelete: {
    width: theme.spacing(16)
  }
});

export const SeasonHome = withStyles(homeStyle)(({ classes, ...props }) => {
    const {roles, hasAccess} = props
  return (
    <List 
    actions={({basePath, roles, hasAccess}) => (
      <div>
        {hasPrivilege(roles, hasAccess, "season", "write") && <CreateButton basePath={basePath} showLabel={false} />}
      </div>
    )}
    {...props}>
      <Datagrid>
        <TextField source="name" />
        <TextField source="desc" />

        {hasPrivilege(roles, hasAccess, "season", "update") && <EditButton cellClassName={classes.buttonEdit} />}
        {hasPrivilege(roles, hasAccess, "season", "delete") && <DeleteButton cellClassName={classes.buttonDelete} />}
      </Datagrid>
    </List>
  );
});
