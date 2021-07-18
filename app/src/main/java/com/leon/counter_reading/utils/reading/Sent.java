package com.leon.counter_reading.utils.reading;

import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.MyDatabaseClient;

import static com.leon.counter_reading.MyApplication.readingData;

public class Sent extends AsyncTask<OnOffLoadDto.OffLoadResponses, Integer, Integer> {
    public Sent() {
        super();
    }

    @Override
    protected Integer doInBackground(OnOffLoadDto.OffLoadResponses... offLoadResponses) {
        try {
            //TODO
            MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().offLoadReportDao().updateOffLoadReportByIsSent(true);
            int state = offLoadResponses[0].isValid ? OffloadStateEnum.SENT.getValue() :
                    OffloadStateEnum.SENT_WITH_ERROR.getValue();
            MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().onOffLoadDao().updateOnOffLoad(state, offLoadResponses[0].targetObject);

            for (String s : offLoadResponses[0].targetObject) {
                for (int j = 0; j < readingData.onOffLoadDtos.size(); j++) {
                    if (s.equals(readingData.onOffLoadDtos.get(j).id)) {
                        readingData.onOffLoadDtos.get(j).offLoadStateId = state;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}