import React from "react";
import { withStyles } from "@material-ui/styles";
import {
  Create,
  Edit,
  List,
  SimpleForm,
  TextInput,
  ReferenceInput,
  SelectInput,
  TextField,
  ReferenceField,
  required,
  minLength,
  Datagrid,
  EditButton,
  DeleteButton
} from "jazasoft";

export const EditTeam = ({ ...props }) => {
  return (
    <Edit {...props}>
      <SimpleForm>
        <ReferenceInput
          source="departmentId"
          reference="departments"
          validate={required()}
        >
          <SelectInput optionText="name" />
        </ReferenceInput>
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Edit>
  );
};

export const CreateTeam = ({ ...props }) => {
  return (
    <Create {...props}>
      <SimpleForm redirect="home">
        <ReferenceInput
          source="departmentId"
          reference="departments"
          validate={required()}
        >
          <SelectInput optionText="name" />
        </ReferenceInput>
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Create>
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

export const TeamHome = withStyles(homeStyle)(({ classes, ...props }) => {
  return (
    <List {...props}>
      <Datagrid>
        <TextField source="name" />
        <ReferenceField source="departmentId" reference="departments">
          <TextField source="name" />
        </ReferenceField>
        <TextField source="desc" />
        <EditButton cellClassName={classes.buttonEdit} />
        <DeleteButton cellClassName={classes.buttonDelete} />
      </Datagrid>
    </List>
  );
});
