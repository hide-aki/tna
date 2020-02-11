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
  DeleteButton,
  Filter,
  CreateButton
} from "jazasoft";
import hasPrivilege from "../../utils/hasPrivilege";
import Forbidden from "../../components/Forbidden";

const filters = (
  <Filter
    parse={({ departmentId }) => ({
      "department.id": departmentId
    })}
  >
    <ReferenceInput source="departmentId" reference="departments" xs={12} fullWidth={true} options={{ fullWidth: true }}>
      <SelectInput optionText="name" />
    </ReferenceInput>
  </Filter>
);

export const EditTeam = ({ ...props }) => {
  if (!hasPrivilege(props.roles, props.hasAccess, "team", "update")) {
    return <Forbidden history={props.history} />;
  }
  return (
    <Edit {...props}>
      <SimpleForm>
        <ReferenceInput source="departmentId" reference="departments" validate={required()}>
          <SelectInput optionText="name" />
        </ReferenceInput>
        <TextInput source="name" validate={[required(), minLength(2)]} />
        <TextInput source="desc" />
      </SimpleForm>
    </Edit>
  );
};

export const CreateTeam = ({ ...props }) => {
  if (!hasPrivilege(props.roles, props.hasAccess, "team", "create")) {
    return <Forbidden history={props.history} />;
  }
  return (
    <Create {...props}>
      <SimpleForm redirect="home">
        <ReferenceInput source="departmentId" reference="departments" validate={required()}>
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
  const { roles, hasAccess } = props;
  return (
    <List
      filters={filters}
      actions={({ basePath, roles, hasAccess }) => (
        <div>{hasPrivilege(roles, hasAccess, "team", "write") && <CreateButton basePath={basePath} showLabel={false} />}</div>
      )}
      {...props}
    >
      <Datagrid>
        <TextField source="name" />
        <ReferenceField source="departmentId" reference="departments">
          <TextField source="name" />
        </ReferenceField>
        <TextField source="desc" />
        {hasPrivilege(roles, hasAccess, "team", "update") && <EditButton cellClassName={classes.buttonEdit} />}
        {hasPrivilege(roles, hasAccess, "team", "delete") && <DeleteButton cellClassName={classes.buttonDelete} />}
      </Datagrid>
    </List>
  );
});
