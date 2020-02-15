import React, { Component } from "react";
import { connect } from "react-redux";
import {
  Show,
  TextField,
  ShowCard,
  MultiCardShowLayout,
  ReferenceField,
  PageFooter,
  Button,
  BackButton,
  crudGetOne,
  RestMethods,
  FETCH_START,
  FETCH_END,
  SAVING_START,
  SAVING_END,
  showNotification
} from "jazasoft";
import { dataProvider } from "../../App";
import handleError from "../../utils/handleError";
import Table, { TextInput } from "jazasoft/lib/mui/components/Table";
import { SelectInput } from "../../components/Table";
import EditIcon from "@material-ui/icons/Edit";
import MaterialTable from "material-table";
import { Icons } from "../../components/MaterialTableIcons";
import hasPrivilege from "../../utils/hasPrivilege";
import ApproveIcon from "@material-ui/icons/Check";
import SaveIcon from "@material-ui/icons/Save";
import BackIcon from "@material-ui/icons/KeyboardBackspace";
import { withStyles } from "@material-ui/core/styles";
import isEqual from "lodash/isEqual";
import PerfectScrollbar from "react-perfect-scrollbar";
import { Role } from "../../utils/types";

const matActivityColumns = approvalStatus => {
  return approvalStatus
    ? [
        { field: "name", title: "Name" },
        { field: "leadTime", title: "Lead Time", cellStyle: { textAlign: "right", paddingRight: "3.5em" }, headerStyle: { textAlign: "right" } }
      ]
    : [
        { field: "name", title: "Name" },
        { field: "prevLeadTime", title: "Prev Lead Time", emptyValue: "-", cellStyle: { paddingLeft: "10em" }, headerStyle: { paddingLeft: "8em" } },
        { field: "leadTime", title: "Lead Time", cellStyle: { textAlign: "right", paddingRight: "3.5em" }, headerStyle: { textAlign: "right" } }
      ];
};

const muiActivityColumns = (timeline, onChange) => [
  { dataKey: "name", title: "Name" },
  {
    dataKey: "timeFrom",
    title: "From",
    element: (
      <SelectInput
        width={450}
        multiple={true}
        onChange={onChange}
        choices={({ record }) => {
          const tActivityList = timeline.tActivityList;
          const currentId = record.activityId;
          const filteredActList = tActivityList.filter(e => e.activityId < currentId);
          const choices = filteredActList.map(({ activityId, name }) => {
            return {
              id: activityId,
              name
            };
          });
          choices.unshift({ id: "O", name: "Order Date" });
          return choices;
        }}
      />
    )
  },
  { dataKey: "prevLeadTime", title: "Previous Lead Time" },
  {
    dataKey: "leadTime",
    title: "Lead Time",
    align: "right",
    element: <TextInput type="number" onChange={onChange} />
  }
];

const homeStyle = () => ({
  content: {
    margin: "1.5em"
  }
});

const Footer = ({ roles, hasAccess, classes, editMode, approvalStatus, onApprove, onUpdateClick, onBackClick, onSaveClick }) => {
  return (
    <PageFooter>
      {!editMode && <BackButton variant="contained" style={{ marginLeft: "1.5em" }} />}
      {editMode && (
        <Button label="Back" variant="contained" onClick={onBackClick} className={classes.content}>
          <BackIcon />
        </Button>
      )}
      {hasPrivilege(roles, hasAccess, "timeline", "update", "approve") && !editMode && !approvalStatus && (
        <Button variant="contained" label="Update" onClick={onUpdateClick} className={classes.content} color="primary">
          <EditIcon />
        </Button>
      )}
      {editMode && (
        <Button variant="contained" label="Save" onClick={onSaveClick} color="primary">
          <SaveIcon />
        </Button>
      )}
      {hasPrivilege(roles, hasAccess, "timeline", "update", "approve") && !editMode && !approvalStatus && (
        <Button variant="contained" label="Approve" onClick={onApprove} color="primary">
          <ApproveIcon />
        </Button>
      )}
    </PageFooter>
  );
};

const format = timeline => {
  let timelineView =
    timeline.tActivityList &&
    timeline.tActivityList
      .sort((a, b) => a.serialNo - b.serialNo)
      .flatMap(tActivity => {
        return [
          {
            ...tActivity,
            timeFrom:
              tActivity.timeFrom === "O"
                ? ["O"]
                : tActivity.timeFrom
                    .split(",")
                    .map(Number)

                    .map(e => {
                      for (let i = 0; timeline.tActivityList && i < timeline.tActivityList.length; i++) {
                        if (e === timeline.tActivityList[i].id) {
                          return timeline.tActivityList[i] && timeline.tActivityList[i].activityId;
                        }
                      }
                      return "";
                    })
          }
        ];
      });

  return timelineView;
};

class TimelineView extends Component {
  state = {
    editMode: false,
    rowActivity: []
  };

  componentDidMount() {
    this.init();
    this.props.dispatch(crudGetOne("timelines", this.props.id));
  }

  //WARNING! To be deprecated in React v17. Use new lifecycle static getDerivedStateFromProps instead.
  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.timeline, nextProps.timeline)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { timeline } = props;
    if (!timeline) return null;

    let rowActivity = format(timeline);
    this.setState({ rowActivity });
  };

  onChange = ({ rowIdx, column }) => e => {
    let rowActivity = this.state.rowActivity.slice();
    if (column.dataKey === "leadTime") {
      rowActivity[rowIdx][column.dataKey] = e.target.value;
    } else if (column.dataKey === "timeFrom") {
      rowActivity[rowIdx][column.dataKey] = e.target.value;
    }
    this.setState({ rowActivity });
  };

  onApprove = () => {
    const { timeline } = this.props;
    const options = {
      url: `timelines/${this.props.id}`,
      method: "put",
      params: { action: "approve" },
      data: timeline
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        if (response.status === 200 || response.status === 201) {
          this.props.dispatch(showNotification("Timeline approved successfully."));
          this.props.dispatch(crudGetOne("timelines", this.props.id));
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

  onUpdateClick = () => {
    this.setState({ editMode: true });
  };

  onBackClick = () => {
    this.setState({ editMode: this.state.editMode === true ? false : true });
  };

  onSaveClick = () => {
    const { timeline } = this.props;
    const { rowActivity } = this.state;

    let parsedTimeline = {
      ...timeline,
      tActivityList: rowActivity.map(e => ({
        ...e,
        timeFrom: e.timeFrom.join(",")
      }))
    };
    parsedTimeline &&
      parsedTimeline.tActivityList.map(e => {
        if (e.timeFrom.length > 1 && e.timeFrom.includes("O")) {
          this.props.dispatch(showNotification("Time From values should be selection of Order Date or multi selection of Activities"));
        } else {
          this.updateTimeline(parsedTimeline);
        }
        return "";
      });
  };

  updateTimeline = timeline => {
    const options = {
      url: `timelines/${this.props.id}`,
      method: "put",
      params: { action: "approval_edit" },
      data: timeline
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        if (response.status === 200 || response.status === 201) {
          this.props.dispatch(showNotification("Timeline updated successfully."));
          this.props.dispatch(crudGetOne("timelines", this.props.id));
          this.setState({ editMode: false });
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

  render() {
    const { timeline = {}, dispatch, classes, roles = [], basePath, hasAccess, ...props } = this.props;
    const isGrassRootUser = roles.includes(Role.MERCHANT) || roles.includes(Role.USER);
    const { editMode, rowActivity } = this.state;
    const approvalStatus = timeline && timeline.approved;
    return (
      <Show cardWrapper={false} {...props}>
        <MultiCardShowLayout
          footer={
            <Footer
              roles={roles}
              hasAccess={hasAccess}
              classes={classes}
              editMode={editMode}
              basePath={basePath}
              onApprove={this.onApprove}
              onUpdateClick={this.onUpdateClick}
              onSaveClick={this.onSaveClick}
              onBackClick={this.onBackClick}
              approvalStatus={approvalStatus}
            />
          }
        >
          <ShowCard title="Timeline Details">
            <ReferenceField source="buyerId" reference="buyers">
              <TextField source="name" />
            </ReferenceField>
            <TextField source="name" />
            <TextField source="stdLeadTime" label="Standard Lead Time" />
          </ShowCard>
          {editMode === false ? (
            <ShowCard
              title="Activities"
              content={({ record = {} }) => {
                if (!record.tActivityList) return null;
                let data = isGrassRootUser
                  ? record.tActivityList
                      .sort((a, b) => a.serialNo - b.serialNo)
                      .flatMap(({ tSubActivityList, ...tActivity }) => {
                        return [
                          {
                            ...tActivity,
                            leadTime:
                              tActivity.timeFrom && tActivity.timeFrom === "O"
                                ? `Order Date + ${tActivity.leadTime}`
                                : tActivity.timeFrom
                                    .split(",")
                                    .map(e => {
                                      // Tranforming t_activity Id back to activityId
                                      for (let i = 0; i < record.tActivityList.length; i++) {
                                        if (Number(e) === record.tActivityList[i].id) {
                                          return record.tActivityList[i];
                                        }
                                      }
                                      return {};
                                    })
                                    .sort((a, b) => a.serialNo - b.serialNo)
                                    .map(e => e.name)
                                    .join(", "),
                            key: `P-${tActivity.id}`
                          },
                          ...tSubActivityList.map(e => ({
                            ...e,
                            key: `C-${e.id}`,
                            parentKey: `P-${tActivity.id}`
                          }))
                        ];
                      })
                  : record.tActivityList
                      .sort((a, b) => a.serialNo - b.serialNo)
                      .map(({ ...tActivity }) => {
                        return {
                          ...tActivity,
                          leadTime:
                            tActivity.timeFrom && tActivity.timeFrom === "O"
                              ? `Order Date + ${tActivity.leadTime}`
                              : tActivity.timeFrom
                                  .split(",")
                                  .map(e => {
                                    // Tranforming t_activity Id back to activityId
                                    for (let i = 0; i < record.tActivityList.length; i++) {
                                      if (Number(e) === record.tActivityList[i].id) {
                                        return record.tActivityList[i];
                                      }
                                    }
                                    return {};
                                  })
                                  .sort((a, b) => a.serialNo - b.serialNo)
                                  .map(e => e.name)
                                  .join(", ") + ` + ${tActivity.leadTime}`
                        };
                      });
                return (
                  <MaterialTable
                    columns={matActivityColumns(approvalStatus)}
                    data={data}
                    icons={Icons}
                    options={{
                      toolbar: false,
                      search: false,
                      paging: false,
                      header: true
                    }}
                    style={{
                      boxShadow: "none",
                      width: "100%"
                    }}
                    parentChildData={
                      !isGrassRootUser
                        ? null
                        : (row, rows) =>
                            rows.find(a => {
                              return a.key === row.parentKey;
                            })
                    }
                    onTreeExpandChange={this.onTreeExpandChange}
                  />
                );
              }}
            />
          ) : (
            rowActivity.length && (
              <ShowCard
                title="Activities"
                content={({ record = {} }) => {
                  return (
                    <PerfectScrollbar style={{ width: "100%" }}>
                      <Table columns={muiActivityColumns(record, this.onChange)} rows={rowActivity} />
                    </PerfectScrollbar>
                  );
                }}
              />
            )
          )}
        </MultiCardShowLayout>
      </Show>
    );
  }
}

const mapStateToProps = (state, props) => ({
  timeline:
    state.jazasoft.resources["timelines"] && state.jazasoft.resources["timelines"].data && state.jazasoft.resources["timelines"].data[props.id]
});

export default connect(mapStateToProps)(withStyles(homeStyle)(TimelineView));
