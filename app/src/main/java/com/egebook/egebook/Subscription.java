package com.egebook.egebook;

import java.util.Date;
import java.util.Calendar;

public class Subscription {
    int subID;
    Date startTime, expireDate;
    boolean isActive = expireDate.after(Calendar.getInstance().getTime());
}
