import React, { Component } from "react";
import moment from "moment";

import "../../asset/css/react-big-calendar.min.css";
import { Calendar as BigCalendar, momentLocalizer } from "react-big-calendar";

import Paper from "@material-ui/core/Paper";
import withStyles from "@material-ui/styles/withStyles";

import { PageHeader } from "jazasoft";

const localizer = momentLocalizer(moment);

var today = new Date();
var y = today.getFullYear();
var m = today.getMonth();
var d = today.getDate();

const events = [
  {
    title: "All Day Event",
    allDay: true,
    start: new Date(y, m, 1),
    end: new Date(y, m, 1),
    color: "default"
  },
  {
    title: "Meeting",
    start: new Date(y, m, d - 1, 10, 30),
    end: new Date(y, m, d - 1, 11, 30),
    allDay: false,
    color: "green"
  },
  {
    title: "Lunch",
    start: new Date(y, m, d + 7, 12, 0),
    end: new Date(y, m, d + 7, 14, 0),
    allDay: false,
    color: "red"
  },
  {
    title: "Nud-pro Launch",
    start: new Date(y, m, d - 2),
    end: new Date(y, m, d - 2),
    allDay: true,
    color: "azure"
  },
  {
    title: "Birthday Party",
    start: new Date(y, m, d + 1, 19, 0),
    end: new Date(y, m, d + 1, 22, 30),
    allDay: false,
    color: "azure"
  },
  {
    title: "Click for Creative Tim",
    start: new Date(y, m, 21),
    end: new Date(y, m, 22),
    color: "orange"
  },
  {
    title: "Click for Google",
    start: new Date(y, m, 21),
    end: new Date(y, m, 22),
    color: "rose"
  }
];

const styles = {
  root: {},
  content: {
    margin: "1.5em"
  }
};

class Calendar extends Component {
  onSelectEvent = event => {
    console.log({ event });
  };

  onSelectSlot = slotInfo => {
    console.log({ slotInfo });
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
    return (
      <div>
        <PageHeader title="My Calendar" />

        <Paper className={classes.content}>
          <BigCalendar
            selectable
            localizer={localizer}
            events={events}
            defaultView="month"
            scrollToTime={new Date(1970, 1, 1, 6)}
            defaultDate={new Date()}
            onSelectEvent={this.onSelectEvent}
            onSelectSlot={this.onSelectSlot}
            eventPropGetter={this.eventColors}
          />
        </Paper>
      </div>
    );
  }
}

export default withStyles(styles)(Calendar);
