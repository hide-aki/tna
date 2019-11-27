import moment from 'moment';

export const totalPagesExp = "{total_pages_count_string}";

export const minReqHeight = 50;

export const pageContent = (doc, heading, date) => {
  var currentpage = 0;
  return data => {
    if (currentpage < doc.internal.getNumberOfPages()) {
      if (heading) {
        doc.setFontSize(12);
        doc.text(heading, doc.internal.pageSize.getWidth() / 2, 10, "center");
      }
      doc.setFont("Courier", "");
      doc.setFontSize(8);
      let today;
      if (date && typeof date === "number") {
        today = new moment(date).format("DD-MM-YYYY h:mm a");
      } else {
        today = new moment().format("DD-MM-YYYY h:mm a");
      }
      doc.text("Date: " + today, doc.internal.pageSize.getWidth() - 7, 5, "right");

      var str = "Page " + doc.internal.getNumberOfPages();
      str = str + " of " + totalPagesExp;
      doc.text(str, doc.internal.pageSize.getWidth() / 2 + 20, doc.internal.pageSize.getHeight() - 7, "center");

      currentpage = doc.internal.getNumberOfPages();
    }
    doc.setFont("helvetica", "normal");
  };
};

export const setDefault = (doc, heading, date) => {
  doc.autoTableSetDefaults({
    theme: "grid",
    margin: { horizontal: 7 },
    tableWidth: "auto",
    showHeader: "firstPage",
    styles: { overflow: "linebreak", fontSize: 8, cellPadding: 1 },
    addPageContent: pageContent(doc, heading, date)
  });
};
