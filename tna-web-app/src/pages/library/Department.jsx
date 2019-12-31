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
  EditButton,
  DeleteButton
} from "jazasoft";

const homeStyle = theme => ({
  buttonEdit: {
    width: theme.spacing(14)
  },
  buttonDelete: {
    width: theme.spacing(16)
  }
});

export const EditDepartment = ({ ...props }) => {
  return (
    <Edit {...props}>
      <SimpleForm>
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Edit>
  );
};

export const CreateDepartment = ({ ...props }) => {
    return (
        <Create {...props}>
            <SimpleForm redirect="home">
                <TextInput source = "name" validate = {[required(), minLength(2)]} />
                <TextInput source = "desc" />
            </SimpleForm>
        </Create>
    )
}

export const DepartmentHome = withStyles(homeStyle)(({ classes, ...props }) => {
  return (
    <List {...props}>
      <Datagrid>
        <TextField source="name" />
        <TextField source="desc" />
        <EditButton cellClassName={classes.buttonEdit} />
        <DeleteButton cellClassName={classes.buttonDelete} />
      </Datagrid>
    </List>
  );
});