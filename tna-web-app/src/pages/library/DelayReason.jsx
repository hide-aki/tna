import React from "react";
import { withStyles } from "@material-ui/core/styles";
import {
  Create,
  Edit,
  List,
  SimpleForm,
  ReferenceInput,
  TextInput,
  TextField,
  required,
  minLength,
  Datagrid,
  SelectInput,
  ReferenceField,
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

export const EditDelayReason = ({ ...props }) => {
  return (
    <Edit {...props}>
      <SimpleForm redirect="home">
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <ReferenceInput
          source="activityId"
          reference="activities"
          validate={required()}
        >
          <SelectInput optionText="name" />
        </ReferenceInput>
      </SimpleForm>
    </Edit>
  );
};

export const CreateDelayReason = ({ ...props }) => {
  return (
    <Create {...props}>
      <SimpleForm redirect="home">
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <ReferenceInput
          source="activityId"
          reference="activities"
          validate={required()}
        >
          <SelectInput optionText="name" />
        </ReferenceInput>
      </SimpleForm>
    </Create>
  );
};

export const DelayReasonHome = withStyles(homeStyle)(
  ({ classes, ...props }) => {
    return (
      <List {...props}>
        <Datagrid>
          <TextField source="name" />
          <ReferenceField source="activityId" reference="activities">
            <TextField source="name" />
          </ReferenceField>
          <EditButton cellClassName={classes.buttonEdit} />
          <DeleteButton cellClassName={classes.buttonDelete} />
        </Datagrid>
      </List>
    );
  }
);
