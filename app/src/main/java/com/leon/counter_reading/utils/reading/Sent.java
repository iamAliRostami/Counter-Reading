package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.readingData;

import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;

public class Sent extends AsyncTask<OnOffLoadDto.OffLoadResponses, Integer, Integer> {
    public Sent() {
        super();
    }

    @Override
    protected Integer doInBackground(OnOffLoadDto.OffLoadResponses... offLoadResponses) {
        try {
            //TODO
            MyApplication.getApplicationComponent().MyDatabase().offLoadReportDao().updateOffLoadReportByIsSent(true);
            int state = offLoadResponses[0].isValid ? OffloadStateEnum.SENT.getValue() :
                    OffloadStateEnum.SENT_WITH_ERROR.getValue();
            MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao()
                    .updateOnOffLoad(state, offLoadResponses[0].targetObject);
            String[] targetObject = offLoadResponses[0].targetObject;
            for (String s : targetObject) {
                for (int j = 0; j < readingData.onOffLoadDtos.size(); j++) {
                    if (s.equals(readingData.onOffLoadDtos.get(j).id)) {
                        readingData.onOffLoadDtos.get(j).offLoadStateId = state;
                    }
                }
            }
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }
}