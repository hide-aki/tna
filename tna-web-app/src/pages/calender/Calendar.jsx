import React, { Component } from "react";
import moment from "moment";
import { dataProvider } from "../../App";
import { RestMethods } from "jazasoft";
import { getDistinctValues } from "../../utils/helpers";
import CalendarFormDialog from "./CalendarFormDialog";

import "../../asset/css/react-big-calendar.min.css";
import { Calendar as BigCalendar, momentLocalizer } from "react-big-calendar";

import Paper from "@material-ui/core/Paper";
import withStyles from "@material-ui/styles/withStyles";

import { PageHeader } from "jazasoft";

const localizer = momentLocalizer(moment);

var today = new Date();
// var y = today.getFullYear();
// var m = today.getMonth();
// var d = today.getDate();

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
      return "default";
    case Category.DUE_DELAYED:
      return "red";
    case Category.COMPLETED_ON_TIME:
      return "green";
    case Category.COMPLETED_DELAYED:
      return "orange";
    default:
      return "default";
  }
};

const styles = {
  root: {},
  content: {
    margin: "1.5em"
  }
};

class Calendar extends Component {
  state = {
    monthEvents: [],
    weekAndDayEvents: [],
    dialogActive: false,
    formData: [],
    view: "month" //month, week, day
  };

  componentDidMount() {
    this.init();
  }

  init = async () => {
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1).getTime();
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1).getTime();
    this.fetchEvents(firstDay, lastDay);
  };

  onNavigate = nav => {
    const firstDay = new Date(moment(nav).startOf("month")).getTime();
    const lastDay = new Date(moment(nav).endOf("month")).getTime();
    this.fetchEvents(firstDay, lastDay);
  };

  fetchEvents = async (firstDay, lastDay) => {
    const options = {
      url: "calendar",
      method: "GET",
      params: { search: `dueDate=gt=${firstDay};dueDate=lt=${lastDay}`, action: `full` }
    };

    try {
      await dataProvider(RestMethods.CUSTOM, null, options).then(response => {
        if (response.status === 200 || response.status === 201) {
          this.formatDefault(response && response.data);
          this.customFormat(response && response.data);
          return;
        }
      });
    } catch (err) {
      console.log(err);
    }
  };

  /**
   * input - List of Task
   * output: {
   *    [date]: {
   *        [category]: filtered list of task
   *    }
   * }
   */
  formatDefault = data => {
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
          title: `${result[date][category].length} tasks - ${category}`,
          start: date,
          end: date,
          allDay: true,
          color: getColor(category),
          taskList: result[date][category]
        }))
      )
      .filter(e => e.taskList.length > 0);

    this.setState({ monthEvents: events });
  };

  customFormat = data => {
    // Initializing view for month & week
    let events =
      data &&
      data.flatMap(e => ({
        ...e,
        title: e.name,
        start: moment(e.dueDate).format("ll"),
        end: moment(e.dueDate).format("ll"),
        allDay: true,
        color: moment(e.completedDate).isSameOrBefore(moment(e.dueDate).endOf("day"))
          ? "green"
          : moment(e.completedDate).isAfter(moment(e.dueDate))
          ? "orange"
          : moment()
              .startOf("day")
              .isSameOrBefore(moment(e.dueDate).endOf("day"))
          ? "blue"
          : "red"
      }));
    this.setState({ weekAndDayEvents: events });
  };

  onSelectEvent = event => {
    this.setState({ dialogActive: true, formData: event.taskList ? event.taskList : event });
  };

  onSelectSlot = slotInfo => {
    console.log({ slotInfo });
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

  render() {
    const { classes } = this.props;
    const { monthEvents, weekAndDayEvents, view, dialogActive, formData } = this.state;

    return (
      <div>
        <CalendarFormDialog open={dialogActive} data={formData} onClose={_ => this.setState({ dialogActive: false })} />
        <PageHeader title="My Calendar" />
        <Paper className={classes.content}>
          <BigCalendar
            selectable
            localizer={localizer}
            events={view === "month" ? monthEvents : weekAndDayEvents}
            defaultView="month"
            views={["month", "week", "day"]}
            scrollToTime={new Date(1970, 1, 1, 6)}
            defaultDate={new Date()}
            onSelectEvent={this.onSelectEvent}
            onSelectSlot={this.onSelectSlot}
            eventPropGetter={this.eventColors}
            onView={this.onView}
            onNavigate={this.onNavigate}
          />
        </Paper>
      </div>
    );
  }
}

export default withStyles(styles)(Calendar);
