package com.leon.counter_reading.utils.backup_restore;

import com.leon.counter_reading.tables.OffLoadReport;

public class OffLoadReportTemp {
    public String onOffLoadId;
    public int reportId;
    public int isSent;
    public int trackNumber;

    public OffLoadReport getOffLoadReport() {
        OffLoadReport offLoadReport = new OffLoadReport();
        offLoadReport.onOffLoadId = onOffLoadId;
        offLoadReport.reportId = reportId;
        offLoadReport.isSent = isSent == 1;
        offLoadReport.trackNumber = trackNumber;
        return offLoadReport;
    }
}
