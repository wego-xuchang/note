## Map的EntryKey

阿里开发手册关于hashcode和equals方法开发规范介绍

1） 只要重写 equals，就必须重写 hashCode。

2） 因为 Set 存储的是不重复的对象，依据 hashCode 和 equals 进行判断，所以 Set 存储的对象必须重写这两个方法。

3） 如果自定义对象作为 Map 的键，那么必须重写 hashCode 和 equals。

说明：String 重写了 hashCode 和 equals 方法，所以我们可以非常愉快地使用 String 对象作为 key 来使用



```java
package com.hashtech.attendance.pojo;

import java.util.Date;
import java.util.Objects;

public class EmpEntryKey {
    private String userId;
    private Date workDate;

    public EmpEntryKey(String userId, Date workDate) {
        this.userId = userId;
        this.workDate = workDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmpEntryKey that = (EmpEntryKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(workDate, that.workDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, workDate);
    }
}

```

存取数据到Map

```java
//将所有用户的考勤记录存入到数据库中
Map<EmpEntryKey, List<OapiAttendanceListResponse.Recordresult>> record = new HashMap<>();
//KEY:USER_ID+WORK_DATE VALUE:OapiAttendanceListResponse.Recordresult
for(OapiAttendanceListResponse.Recordresult recordresult : allRecordResults){
    EmpEntryKey empEntryKey = new EmpEntryKey(recordresult.getUserId(), recordresult.getWorkDate());
    if(record.containsKey(empEntryKey)){
        record.get(empEntryKey).add(recordresult);
    } else {
        List<OapiAttendanceListResponse.Recordresult> list = new ArrayList<>();
        list.add(recordresult);
        record.put(empEntryKey, list);
    }
}

```

```java
private List<SdkRecordContractBarGraphResult> getResult(List<SdkRecordContractBarGraphResultDTO> results,List<Date> everyDay){

    Map<BarGraphEntryKey,List<SdkRecordContractBarGraphResultDTO>> allResult = new HashMap<>();
    for (SdkRecordContractBarGraphResultDTO result:results) {
        String projectName = result.getProjectName();
        Date createTime =DateCommonUtils.getFirstSecondOfDate(result.getCreateTime()) ;
        BarGraphEntryKey key = new BarGraphEntryKey(projectName,createTime);
        if (allResult.containsKey(key)){
            allResult.get(key).add(result);
        }else {
            List<SdkRecordContractBarGraphResultDTO> record = new ArrayList<>();
            record.add(result);
            allResult.put(key,record);
        }
    }

    List<SdkRecordContractBarGraphResult> resultList = new ArrayList<>();
    for (Date day:everyDay) {
        SdkRecordContractBarGraphResult dayResult = new SdkRecordContractBarGraphResult();
        dayResult.setDate(day);
        List<BarGraphResult> barGraphResults = new ArrayList<>();
        for (Map.Entry<BarGraphEntryKey, List<SdkRecordContractBarGraphResultDTO>> entry : allResult.entrySet()) {
            BarGraphEntryKey key = entry.getKey();
            Integer count = entry.getValue().size();
            if (key.getDate().getTime() == day.getTime()){
                BarGraphResult barGraphResult = new BarGraphResult();
                barGraphResult.setProjectName(key.getProjectName());
                barGraphResult.setCount(count);
                barGraphResults.add(barGraphResult);
            }
        }
        dayResult.setBarGraphResults(barGraphResults);
        resultList.add(dayResult);
    }
    return resultList;
}

```

