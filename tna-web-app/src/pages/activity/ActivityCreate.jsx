import React, { Component } from "react";

import withStyles from "@material-ui/styles/withStyles";

import {
  Create,
  MultiCardForm,
  FormCard,
  TextInput,
  NumberInput,
  ReferenceInput,
  SelectInput,
  BooleanInput,
  ArrayInput,
  SimpleFormIterator,
  required,
  minLength,
} from "jazasoft";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const styles = {};

class ActivityCreate extends Component {
  render() {
    const { ...props } = this.props;
    return (
      <Create
        title="Generate Activity and subactivities"
        cardWrapper={false}
        record={{ subActivityList: [{}] }}
        {...props}
      >
        <MultiCardForm redirect="home">
          <FormCard title="Activity Details">
            <NumberInput source="serialNo" validate={[required()]} {...inputOptions(3)}/>
            <TextInput source="name" validate={[required(), minLength(2)]} {...inputOptions(3)}/>
            <ReferenceInput
              source="departmentId"
              reference="departments"
              {...inputOptions(3)}
            >
              <SelectInput optionText="name" />
            </ReferenceInput>

            <TextInput source="notify" {...inputOptions(3)} />
            <BooleanInput source="cLevel" />
          </FormCard>
          <FormCard title="Subactivities">
            <ArrayInput
              label="Subactivity List"
              source="subActivityList"
              xs={12}
              fullWidth={true}
              validate={required()}
            >
              <SimpleFormIterator>
                <TextInput
                  label="Name"
                  source="name"
                  validate={[required(), minLength(2)]}
                  {...inputOptions(4)}
                />
                <TextInput
                  label="Description"
                  source="desc"
                  {...inputOptions(8)}
                />
              </SimpleFormIterator>
            </ArrayInput>
          </FormCard>
        </MultiCardForm>
      </Create>
    );
  }
}
export default withStyles(styles)(ActivityCreate);
