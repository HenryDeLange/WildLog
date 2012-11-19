package wildlog.ui.panels.bulkupload.data;

import java.util.Date;

public class BulkUploadDataWrapper {
        private Object[][] data;
        private Date startDate;
        private Date endDate;

        public Object[][] getData() {
            return data;
        }

        public void setData(Object[][] inData) {
            data = inData;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date inEndDate) {
            endDate = inEndDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date inStartDate) {
            startDate = inStartDate;
        }

    }
