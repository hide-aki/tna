import React, { Component } from "react";
import { connect } from "react-redux";
import isEqual from "lodash/isEqual";
import { Field, Fields, FieldArray } from "redux-form";

//Material-UI
import { withStyles } from "@material-ui/styles";
import {
  Typography,
  ExpansionPanelSummary,
  ExpansionPanelDetails,
  ExpansionPanel,
  Card,
  Divider,
  Paper,
  Dialog,
  DialogTitle,
  List,
  ListItem,
  ListItemText,
  FormControlLabel
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import RemoveIcon from "@material-ui/icons/HighlightOff";
import SaveIcon from "@material-ui/icons/Save";
import AddIcon from "@material-ui/icons/Add";
import hasPrivilege from "../../utils/hasPrivilege";
import Forbidden from "../../components/Forbidden";

import {
  PageHeader,
  PageFooter,
  TextInput,
  NumberInput,
  ReferenceInput,
  SelectInput,
  ArrayInput,
  SimpleFormIterator,
  required,
  WithReduxForm,
  Button,
  BackButton,
  crudGetList,
  RestMethods,
  minValue,
  maxValue,
  maxLength,
  SelectArrayInput,
  showNotification,
  FormDataConsumer,
  SAVING_START,
  SAVING_END,
  FETCH_START,
  FETCH_END
} from "jazasoft";
import { SimpleForm } from "jazasoft/lib/mui/form/SimpleForm";
import CardHeader from "../../components/CardHeader";
import { dataProvider } from "../../App";
import handleError from "../../utils/handleError";

// Styling
const homeStyle = theme => ({
  container: {
    margin: theme.spacing(3)
  },
  panelBtn: {
    width: "100%",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center"
  },
  card: {
    margin: "1em 0"
  },
  heading: {
    fontSize: theme.typography.pxToRem(15),
    fontWeight: theme.typography.fontWeightRegular
  },
  timelineField: {
    padding: "1.5em"
  },
  activitiesField: {
    padding: "1em"
  },
  addBtn: {
    marginRight: "1em"
  },
  backBtn: {
    marginLeft: "1em"
  },
  form: {
    width: "100%"
  },
  radioBtn: {
    marginLeft: "3em",
    marginTop: "3em",
    "&>*": {
      display: "flex",
      flexDirection: "row"
    }
  }
});

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true
});

// Formatting structure for Redux-Fields
const format = activityList => {
  let timeline = {
    tActivityList:
      activityList &&
      activityList.map(({ id, name, serialNo, subActivityList }) => ({
        activityId: id,
        timeFrom: ["O"],
        name,
        serialNo,
        tSubActivityList:
          subActivityList &&
          subActivityList.map(({ id, name }) => ({
            subActivityId: id,
            name
          }))
      }))
  };
  return timeline;
};

// To populate Activity name on Expansion Panel
const TextField = ({ className, input: { value } }) => <Typography className={className}>{value}</Typography>;

// Add Activity Dialog box Content
const SelectDialog = ({ open, onClose, data, fields, onSelect }) => {
  return (
    <Dialog onClose={onClose} open={open} maxWidth="xs" fullWidth>
      <DialogTitle>Activities</DialogTitle>
      <Divider />
      <List>
        {data.map((activity, idx) => (
          <ListItem divider button onClick={_ => onSelect(fields, activity)} key={idx}>
            <ListItemText primary={activity.name} />
          </ListItem>
        ))}
        {data.length === 0 && (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              padding: "1em"
            }}
          >
            No data available
          </div>
        )}
      </List>
    </Dialog>
  );
};

// Render Activity Field Panels
const renderActivities = ({ fields, activities, classes, expanded, handleExpansionPanelChange, onAddActivity, onRemoveActivity }) => {
  return (
    <Card className={classes.card}>
      <div className={classes.panelBtn}>
        <CardHeader title="Activities" />
        <Button showLabel={false} label="Add Activity" className={classes.addBtn} onClick={_ => onAddActivity(fields)}>
          <AddIcon />
        </Button>
      </div>
      <Divider />
      <Paper className={classes.activitiesField}>
        {fields.map((activity, idx) => {
          const activityObj = (activities && fields.get(idx) && activities[fields.get(idx).activityId]) || {};
          return (
            <ExpansionPanel key={idx} expanded={expanded === activityObj.id} onChange={handleExpansionPanelChange(activityObj.id)}>
              <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
                <div className={classes.panelBtn}>
                  <Field name={`${activity}.name`} component={TextField} className={classes.heading} />
                  <div style={{ display: "flex", flexDirection: "row", alignItems: "center" }}>
                    <Fields
                      names={[`${activity}.timeFrom`, `${activity}.leadTime`]}
                      component={({ names, tActivityList }) => {
                        const source = names[0];
                        let idx = source && source.split(".")[0].substring(14);
                        idx = idx.substring(0, idx.length - 1);

                        const activity = tActivityList[idx];

                        const leadTime = activity ? activity.leadTime.input.value : null;
                        let timeFrom = activity ? activity.timeFrom.input.value : [];
                        timeFrom = timeFrom.map(e => (e === "O" ? "Order Date" : activities[e] && activities[e].name));

                        return timeFrom.length > 0 && leadTime ? (
                          <div style={{ display: "flex", flexDirection: "row", alignItems: "center", marginRight: "3em" }}>
                            <div> {timeFrom.join(", ")} </div>
                            <div>&nbsp;+&nbsp; </div>
                            <div> {leadTime} </div>
                          </div>
                        ) : null;
                      }}
                    />

                    <FormControlLabel
                      onClick={event => event.stopPropagation()}
                      onFocus={event => event.stopPropagation()}
                      control={
                        <Button showLabel={false} label="Remove Activity" onClick={_ => onRemoveActivity(fields, idx, activity)}>
                          <RemoveIcon />
                        </Button>
                      }
                    />
                  </div>
                </div>
              </ExpansionPanelSummary>
              <ExpansionPanelDetails>
                <SimpleForm component="div" className={classes.form} footer={false}>
                  <FormDataConsumer {...inputOptions(6)}>
                    {({ formData }) => {
                      const formActList = formData.tActivityList;
                      const currentId = activityObj.id;
                      const filteredActList = formActList.filter(e => e.activityId < currentId);
                      const choices = filteredActList.map(({ activityId, name }) => {
                        return {
                          id: activityId,
                          name
                        };
                      });
                      choices.unshift({ id: "O", name: "Order Date" });
                      return (
                        <SelectArrayInput source={`${activity}.timeFrom`} label="From" choices={choices} {...inputOptions(6)} validate={required()} />
                      );
                    }}
                  </FormDataConsumer>
                  <NumberInput label="Lead Time" source={`${activity}.leadTime`} validate={[required(), minValue(1)]} {...inputOptions(6)} />
                  <ArrayInput label="Subactivity List" source={`${activity}.tSubActivityList`} {...inputOptions(12)}>
                    <SimpleFormIterator>
                      {activityObj && activityObj.subActivityList && (
                        <SelectInput
                          key={idx}
                          source="subActivityId"
                          label="Sub Activities"
                          choices={activityObj.subActivityList.map(({ id, name }) => ({
                            id: id,
                            name
                          }))}
                          {...inputOptions(6)}
                          validate={required()}
                        />
                      )}

                      <NumberInput source="leadTime" label="Lead Time" {...inputOptions(6)} validate={[required(), maxValue(0)]} />
                    </SimpleFormIterator>
                  </ArrayInput>
                </SimpleForm>
              </ExpansionPanelDetails>
            </ExpansionPanel>
          );
        })}
      </Paper>
    </Card>
  );
};

class TimelineCreate extends Component {
  state = {
    activityList: [], // Selected activity List
    rActivityList: [], // Remaining activity List
    initialValues: {},
    dialogActive: false,
    expanded: null
  };

  componentDidMount() {
    this.props.dispatch(
      crudGetList("activities", null, null, null, null, {
        params: { action: "timeline" }
      })
    );
    this.init();
  }

  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.activities, nextProps.activities)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { activities } = props;
    const defaultActivityList = activities // Filtering Default Activities
      ? activities &&
        Object.keys(activities)
          .map(id => activities[id])
          .filter(e => e.isDefault)
          .sort((a, b) => a.serialNo - b.serialNo)
      : [];
    const initialValues = format(defaultActivityList);
    this.setState({
      activityList: defaultActivityList, // Activities present on Redux-form Expansion Panel
      initialValues
    });
  };

  onRemoveActivity = (fields, index) => {
    let activityList = this.state.activityList.slice();
    activityList.splice(index, 1);
    this.setState({ activityList: activityList });
    fields.remove(index);
  };

  onAddActivity = fields => {
    const { activities } = this.props;
    const { activityList } = this.state;
    const totalActivityList = activities ? Object.keys(activities).map(id => activities[id]) : [];
    // Ids of activities present in the current state and Redux-form
    const selectedIds = activityList.map(e => e.id);
    // Remaining activities after filtering out state/current activities from whole activity list
    const rActivityList = totalActivityList.filter(e => !selectedIds.includes(e.id)).sort((a, b) => a.serialNo - b.serialNo);
    this.setState({ dialogActive: true, rActivityList, fields });
  };

  onSubmit = handleSubmit => {
    handleSubmit(values => {
      this.parse(values);
    })();
  };

  // Parsing submission values to convert timeFrom values to CSV
  parse = values => {
    const { tActivityList, ...rest } = values;
    let parsedValue = {
      ...rest,
      tActivityList: tActivityList.map(activity => ({
        ...activity,
        timeFrom: activity.timeFrom.join()
      }))
    };
    this.createTimeline(parsedValue);
  };

  createTimeline = timeline => {
    const options = {
      url: "timelines",
      method: "post",
      data: timeline
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        if (response.status === 200 || response.status === 201) {
          this.props.dispatch(showNotification("Timeline created successfully."));
          this.props.history.push("/timelines");
        }
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      })
      .catch(error => {
        handleError(error, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  onValidate = values => {
    const errors = {};
    const tActivityList = [];
    values &&
      values.tActivityList &&
      values.tActivityList.forEach((activity, activityIdx) => {
        const tActivity = {};
        let timeFromLength = activity.timeFrom ? activity.timeFrom.length : 0;
        for (let i = 0; i < timeFromLength; i++) {
          for (let j = 1; j < timeFromLength; j++) {
            if (typeof activity.timeFrom[i] !== typeof activity.timeFrom[j]) {
              tActivity.timeFrom = "Value should be either single selection of Order date or multi-selection of activities";
              tActivityList[activityIdx] = tActivity;
            } else if (typeof activity.timeFrom[i] === "string" && typeof activity.timeFrom[j] === "string") {
              tActivity.timeFrom = "Value should be either single selection of Order date or multi-selection of activities";
              tActivityList[activityIdx] = tActivity;
            }
          }
        }
        const tSubActivityList = [];
        let sortedSubActivityList = activity.tSubActivityList && activity.tSubActivityList.map(a => a.subActivityId);
        activity.tSubActivityList &&
          activity.tSubActivityList.forEach((subActivity, subActivityIdx) => {
            const tSubActivity = {};
            if (-subActivity.leadTime > activity.leadTime) {
              tSubActivity.leadTime = "Value should be in Activity's Lead Time range";
              tSubActivityList[subActivityIdx] = tSubActivity;
            } else if (sortedSubActivityList.some((a, index) => sortedSubActivityList.indexOf(a) !== index)) {
              tSubActivity.subActivityId = "Subactivities should not be same";
              tSubActivityList[subActivityIdx] = tSubActivity;
            }
          });
        if (tSubActivityList.length) {
          tActivity.tSubActivityList = tSubActivityList;
          tActivityList[activityIdx] = tActivity;
        }
      });
    if (tActivityList.length) {
      errors.tActivityList = tActivityList;
    }
    return errors;
  };

  onSelect = (fields, activity) => {
    let newActivity = {
      activityId: activity.id,
      name: activity.name,
      serialNo: activity.serialNo,
      tSubActivityList:
        activity &&
        activity.subActivityList &&
        activity.subActivityList.map(({ id, name }) => ({
          subActivityId: id,
          name
        }))
    };
    const fieldsList = fields.getAll();
    const fieldsSerialNoList = fieldsList.sort((a, b) => a.serialNo - b.serialNo).map(e => e.serialNo);
    const newActivitySerialNo = newActivity.serialNo;

    var tempIdx = 0; // Computing the index order for new activity
    fieldsSerialNoList.forEach((e, i) => {
      if (newActivitySerialNo > e) {
        tempIdx = i + 1;
      }
    });
    fields.insert(tempIdx, newActivity);
    let activityList = this.state.activityList.slice();
    activityList.splice(tempIdx, 0, activity);
    this.setState({ activityList, dialogActive: false });
  };

  // Expansion bar control
  handleChange = panel => (event, isExpanded) => {
    this.setState({ expanded: isExpanded ? panel : false });
  };

  render() {
    const { roles, hasAccess, history, classes, activities } = this.props;
    const { dialogActive, rActivityList, fields, initialValues, expanded } = this.state;
    if (!hasPrivilege(roles, hasAccess, "timeline", "write")) {
      return <Forbidden history={history} />;
    }
    return (
      <div>
        <PageHeader title="Create Timeline" />
        <SelectDialog
          open={dialogActive}
          onClose={() => this.setState({ dialogActive: false, fields: null })}
          data={rActivityList}
          onSelect={this.onSelect}
          fields={fields}
        />
        <div className={classes.container}>
          <WithReduxForm initialValues={initialValues} validate={this.onValidate}>
            {({ handleSubmit }) => (
              <div>
                <Card className={classes.card}>
                  <CardHeader title="Timeline Details" />
                  <Divider />
                  <div className={classes.timelineField}>
                    <SimpleForm component="div" footer={false}>
                      <ReferenceInput source="buyerId" reference="buyers" {...inputOptions(4)} validate={required()}>
                        <SelectInput optionText="name" />
                      </ReferenceInput>
                      <TextInput source="name" validate={[maxLength(30), required()]} {...inputOptions(3)} />
                    </SimpleForm>
                  </div>
                </Card>

                <FieldArray
                  name="tActivityList"
                  component={renderActivities}
                  activities={activities}
                  classes={classes}
                  expanded={expanded}
                  handleExpansionPanelChange={this.handleChange}
                  onAddActivity={this.onAddActivity}
                  onRemoveActivity={this.onRemoveActivity}
                />

                <PageFooter>
                  <Button label="Save" variant="contained" color="primary" onClick={_ => this.onSubmit(handleSubmit)}>
                    <SaveIcon />
                  </Button>
                  <BackButton style={{ marginLeft: "2em" }} variant="contained" />
                </PageFooter>
              </div>
            )}
          </WithReduxForm>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  activities: state.jazasoft.resources["activities"] && state.jazasoft.resources["activities"].data,
  saving: state.jazasoft.saving
});

export default connect(mapStateToProps)(withStyles(homeStyle)(TimelineCreate));
