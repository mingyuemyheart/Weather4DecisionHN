package com.pmsc.weather4decision.phone.hainan.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 消息推送
 */

public class NewsDto implements Parcelable{

    public String title;
    public String content;
    public String time;
    public String pushType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.time);
        dest.writeString(this.pushType);
    }

    public NewsDto() {
    }

    protected NewsDto(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.time = in.readString();
        this.pushType = in.readString();
    }

    public static final Creator<NewsDto> CREATOR = new Creator<NewsDto>() {
        @Override
        public NewsDto createFromParcel(Parcel source) {
            return new NewsDto(source);
        }

        @Override
        public NewsDto[] newArray(int size) {
            return new NewsDto[size];
        }
    };
}
