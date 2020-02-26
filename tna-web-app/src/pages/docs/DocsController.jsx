import { Component } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import compose from "recompose/compose";
// import isEqual from "lodash/isEqual";

import { translate, RestMethods, FETCH_START, FETCH_END, SAVING_START, SAVING_END } from "jazasoft";

import { dataProvider } from "../../App";
import handleError from "../../utils/handleError";

export class DocsController extends Component {
  state = {
    sections: {},
    subSections: {},
    topics: {}
  };

  fetchTopics = search => {
    const type = this.props.location.pathname.includes("help") ? "Help" : "Manual";
    search = search ? search + ";" : "";
    search += `subSection.section.type=='${type}'`;

    const options = {
      url: `topics`,
      method: "get",
      params: {
        page: 0,
        size: 100,
        search
      }
    };
    this.props.dispatch({ type: FETCH_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        const data = response.data.content;
        const topics = data.reduce((acc, el) => ({ ...acc, [el.id]: el }), {});
        const subSections = data
          .filter(e => e.subSection)
          .reduce((acc, el) => ({ ...acc, [el.subSection.id]: el.subSection }), {});
        const sections = data
          .filter(e => e.subSection && e.subSection.section)
          .reduce((acc, el) => ({ ...acc, [el.subSection.section.id]: el.subSection.section }), {});

        this.setState({
          topics: { ...this.state.topics, ...topics },
          subSections: { ...this.state.subSections, ...subSections },
          sections: { ...this.state.sections, ...sections }
        });
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
      });
  };

  fetchTopic = id => {
    const options = {
      url: `topics/${id}`,
      method: "get"
    };
    this.props.dispatch({ type: FETCH_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        const topic = response.data;
        this.setState({
          topics: { ...this.state.topics, [topic.id]: topic },
          subSections: { ...this.state.subSections, [topic.subSection.id]: topic.subSection },
          sections: { ...this.state.sections, [topic.subSection.section.id]: topic.subSection.section }
        });
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
      });
  };

  fetchSections = search => {
    const type = this.props.location.pathname.includes("help") ? "Help" : "Manual";
    search = search ? search + ";" : "";
    search += `type=='${type}'`;

    const options = {
      url: `sections`,
      method: "get",
      params: {
        page: 0,
        size: 100,
        search
      }
    };
    this.props.dispatch({ type: FETCH_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        const data = response.data.content;
        const sections = data.reduce((acc, el) => ({ ...acc, [el.id]: el }), {});
        this.setState({
          sections: { ...this.state.sections, ...sections }
        });
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
      });
  };

  fetchSubSections = search => {
    const type = this.props.location.pathname.includes("help") ? "Help" : "Manual";
    search = search ? search + ";" : "";
    search += `section.type=='${type}'`;

    const options = {
      url: `subSections`,
      method: "get",
      params: {
        page: 0,
        size: 100,
        search
      }
    };
    this.props.dispatch({ type: FETCH_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        const data = response.data.content;
        const subSections = data.reduce((acc, el) => ({ ...acc, [el.id]: el }), {});
        this.setState({
          subSections: { ...this.state.subSections, ...subSections }
        });
        this.props.dispatch({ type: FETCH_END });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
      });
  };

  saveSection = (section, cb) => {
    const options = {
      url: `sections`,
      method: "post",
      data: section
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        const section = response.data;
        this.setState({ sections: { ...this.state.sections, [section.id]: section } });
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
        cb && cb(section);
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        cb && cb(null, err);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  saveSubSection = (subSection, cb) => {
    const options = {
      url: `subSections`,
      method: "post",
      data: subSection
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        const subSection = response.data;
        this.setState({ subSections: { ...this.state.subSections, [subSection.id]: subSection } });
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
        cb && cb(subSection);
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        cb && cb(null, err);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  saveOrUpdateTopic = (action, topic, cb) => {
    const options = {
      url: `topics${action === "update" ? `/${topic.id}` : ""}`,
      method: action === "update" ? "put" : "post",
      data: topic
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        const topic = response.data;
        this.setState({ topics: { ...this.state.topics, [topic.id]: topic } });
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
        cb && cb(topic);
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        cb && cb(null, err);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  render() {
    const { isLoading, saving, children, version, translate, i18nKey, dispatch } = this.props;

    if (!children) return null;

    return children({
      isLoading,
      saving,
      i18nKey,
      fetchTopics: this.fetchTopics,
      fetchTopic: this.fetchTopic,
      fetchSections: this.fetchSections,
      fetchSubSections: this.fetchSubSections,
      saveSection: this.saveSection,
      saveSubSection: this.saveSubSection,
      saveOrUpdateTopic: this.saveOrUpdateTopic,
      translate,
      version,
      dispatch,
      ...this.state
    });
  }
}

DocsController.propTypes = {
  children: PropTypes.func.isRequired,
  saving: PropTypes.bool,
  isLoading: PropTypes.bool,
  translate: PropTypes.func.isRequired,
  i18nKey: PropTypes.string
};

DocsController.defaultProps = {
  requestConfig: {},
  format: record => record
};

const mapStateToProps = state => {
  return {
    isLoading: state.jazasoft.loading > 0,
    saving: state.jazasoft.saving,
    version: state.jazasoft.ui.viewVersion
  };
};

export default compose(translate, connect(mapStateToProps))(DocsController);
