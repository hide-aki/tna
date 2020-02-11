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

const homeStyle = theme => ({
  buttonEdit: {
    width: theme.spacing(14)
  },
  buttonDelete: {
    width: theme.spacing(16)
  }
});

export const EditGarmentType = ({ ...props }) => {
  if(!hasPrivilege(props.roles, props.hasAccess, "garmentType", "update")){
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
  

export const CreateGarmentType = ({ ...props }) => {
  if(!hasPrivilege(props.roles, props.hasAccess, "garmentType", "write")){
    return <Forbidden history={props.history} />
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

export const GarmentTypeHome = withStyles(homeStyle)(({ classes, ...props }) => {
  const {roles, hasAccess} = props
  return (
      <List 
        actions={({basePath, roles, hasAccess}) => (
          <div>
            {hasPrivilege(roles, hasAccess, "garmentType", "write") && <CreateButton basePath={basePath} showLabel={false} />}
          </div>
        )}
      {...props}>
        <Datagrid>
          <TextField source="name" />
          <TextField source="desc" />
          {hasPrivilege(roles, hasAccess, "garmentType", "update") && <EditButton cellClassName={classes.buttonEdit} />}
          {hasPrivilege(roles, hasAccess, "garmentType", "delete") && <DeleteButton cellClassName={classes.buttonDelete}/>}
        </Datagrid>
      </List>
    );
  }
);
