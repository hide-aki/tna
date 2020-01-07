import React, { Component } from "react";

import withStyles from "@material-ui/styles/withStyles";

import {
  Edit,
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
  minLength
} from "jazasoft";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const styles = {};

class ActivityEdit extends Component {
  render() {
    const { classes, dispatch, activity, ...props } = this.props;
    return (
      <Edit cardWrapper={false} record={{ subActivityList: [{}] }} {...props}>
        <MultiCardForm redirect="home">
          <FormCard title="Activity Details">
            <NumberInput
              source="serialNo"
              validate={[required()]}
              {...inputOptions(3)}
            />
            <TextInput
              source="name"
              validate={[required(), minLength(2)]}
              {...inputOptions(3)}
            />
            <ReferenceInput
              source="departmentId"
              reference="departments"
              {...inputOptions(3)}
              validate={required()}
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
      </Edit>
    );
  }
}

export default withStyles(styles)(ActivityEdit);
