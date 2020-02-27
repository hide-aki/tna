import React, { Component } from "react";
import moment from "moment";
import { dataProvider } from "../../App";
import { RestMethods } from "jazasoft";
import { getDistinctValues } from "../../utils/helpers";
import CalendarFormDialog from "./CalendarFormDialog";

import "../../asset/css/react-big-calendar.css";
import { Calendar as BigCalendar, momentLocalizer } from "react-big-calendar";
import Radio from "@material-ui/core/Radio";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Paper from "@material-ui/core/Paper";
import withStyles from "@material-ui/styles/withStyles";

import { PageHeader } from "jazasoft";

const localizer = momentLocalizer(moment);

const Category = {
  DUE_ON_TIME: "due-on-time",
  DUE_DELAYED: "due-delayed",
  COMPLETED_ON_TIME: "completed-on-time",
  COMPLETED_DELAYED: "completed-delayed"
};

const cbFilter = (task, day, category) => {
  let result = day === moment(task.dueDate).format("ll");
  if (result) {
    switch (category) {
      case Category.DUE_ON_TIME:
        result = !task.completedDate && moment().isSameOrBefore(moment(task.dueDate).endOf("day"));
        break;
      case Category.DUE_DELAYED:
        result = !task.completedDate && moment().isAfter(moment(task.dueDate));
        break;
      case Category.COMPLETED_ON_TIME:
        result = task.completedDate && moment(task.completedDate).isSameOrBefore(moment(task.dueDate).endOf("day"));
        break;
      case Category.COMPLETED_DELAYED:
        result = task.completedDate && moment(task.completedDate).isAfter(moment(task.dueDate));
        break;
      default:
        result = false;
    }
  }
  return result;
};

const getColor = category => {
  switch (category) {
    case Category.DUE_ON_TIME:
      return "blue";
    case Category.DUE_DELAYED:
      return "red";
    case Category.COMPLETED_ON_TIME:
      return "green";
    case Category.COMPLETED_DELAYED:
      return "orange";
    default:
      return "blue";
  }
};

const getLabel = category => {
  switch (category) {
    case Category.DUE_ON_TIME:
      return "Due";
    case Category.DUE_DELAYED:
      return "Due (Not Completed)";
    case Category.COMPLETED_ON_TIME:
      return "Completed";
    case Category.COMPLETED_DELAYED:
      return "Completed (Delayed)";
    default:
      return "blue";
  }
};

const format = data => {
  if (!data) return [];
  const days = getDistinctValues(data.map(task => moment(task.dueDate).format("ll")));
  const categoryList = [Category.DUE_ON_TIME, Category.DUE_DELAYED, Category.COMPLETED_ON_TIME, Category.COMPLETED_DELAYED];
  const result = days.reduce(
    (acc, day) => ({
      ...acc,
      [day]: categoryList.reduce((acc, category) => ({ ...acc, [category]: data.filter(task => cbFilter(task, day, category)) }), {})
    }),
    {}
  );

  const events = Object.keys(result)
    .flatMap(date =>
      Object.keys(result[date]).map(category => ({
        title: `${result[date][category].length} Task${result[date][category].length !== 1 ? "s" : ""} - ${getLabel(category)}`,
        start: date,
        end: date,
        allDay: true,
        color: getColor(category),
        taskList: result[date][category]
      }))
    )
    .filter(e => e.taskList.length > 0);
  return events;
};

const MyAgendaEvent = ({ event }) => (
  <span>
    <b>{event.name} </b>(PO Ref: {event.poRef}, Buyer: {event.buyer}, Season: {event.season}, Style: {event.style}, Order Qty: {event.orderQty})
  </span>
);

const styles = theme => ({
  group: {
    display: "flex",
    flexDirection: "row",
    margin: `${theme.spacing()}px 0`
  },
  content: {
    margin: "1.5em"
  }
});

class Calendar extends Component {
  state = {
    firstDay: moment()
      .startOf("month")
      .valueOf(),
    lastDay: moment()
      .endOf("month")
      .valueOf(),
    value: "activity", //activity view, Full view
    events: [],
    agendaEvents: [], //todo: remove
    dialogActive: false,
    dialogData: [], // Data on event click
    view: "month" //month, week, day
  };

  componentDidMount() {
    this.fetchEvents();
  }

  onNavigate = nav => {
    const firstDay = moment(nav)
      .startOf("month")
      .valueOf();
    const lastDay = moment(nav)
      .endOf("month")
      .valueOf();
    this.fetchEvents(firstDay, lastDay);
    console.log({nav: moment(nav).format("lll"), firstDay: moment(firstDay).format("lll"), lastDay: moment(lastDay).format("lll")});
    
  };

  fetchEvents = (firstDay = this.state.firstDay, lastDay = this.state.lastDay, action = "activity") => {
    const options = {
      url: "calendar",
      method: "GET",
      params: { search: `dueDate=gt=${firstDay};dueDate=lt=${lastDay}`, action }
    };
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        if (response.status === 200 || response.status === 201) {
          const events = format(response && response.data);
          this.setState({ firstDay, lastDay, events });
        }
      })
      .catch(err => {
        console.log(err);
      });
  };

  /**
   * input - List of Task
   * output: {
   *    [date]: {
   *        [category]: filtered list of task
   *    }
   * }
   */

  onSelectEvent = event => {
    this.setState({ dialogActive: true, dialogData: event.taskList ? event.taskList : event });
  };

  onView = view => {
    this.setState({ view });
  };

  eventColors = event => {
    var backgroundColor = "event-";
    event.color ? (backgroundColor = backgroundColor + event.color) : (backgroundColor = backgroundColor + "default");
    return {
      className: backgroundColor
    };
  };

  onViewChange = event => {
    const { firstDay, lastDay } = this.state;
    const value = event.target.value;
    this.setState({ value });
    this.fetchEvents(firstDay, lastDay, value);
  };

  render() {
    const { classes } = this.props;
    const { view, dialogActive, dialogData, value } = this.state;

    const events =
      view === "month"
        ? this.state.events
        : this.state.events.flatMap(({ taskList, ...event }) => (taskList ? taskList.map(e => ({ ...event, ...e })) : []));

    const components = {
      agenda: {
        event: MyAgendaEvent
      }
    };

    return (
      <div>
        {/** Header Element */}
        <PageHeader title="My Calendar">
          <div>
            <RadioGroup aria-label="View" className={classes.group} value={this.state.value} onChange={this.onViewChange}>
              <FormControlLabel value="activity" control={<Radio />} label="Activity Only" />
              <FormControlLabel value="full" control={<Radio />} label="Full" />
            </RadioGroup>
          </div>
        </PageHeader>

        <CalendarFormDialog open={dialogActive} data={dialogData} view={value} onClose={_ => this.setState({ dialogActive: false })} />

        <Paper className={classes.content}>
          <BigCalendar
            selectable
            localizer={localizer}
            events={events}
            defaultView="month"
            views={["month", "agenda"]}
            scrollToTime={new Date(1970, 1, 1, 6)}
            defaultDate={new Date()}
            onSelectEvent={this.onSelectEvent}
            eventPropGetter={this.eventColors}
            onView={this.onView}
            onNavigate={this.onNavigate}
            components={components}
          />
        </Paper>
      </div>
    );
  }
}

export default withStyles(styles)(Calendar);
