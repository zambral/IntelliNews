package news.intelli.intellinews.mvp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by llefoulon on 03/12/2016.
 */

public class Word implements Parcelable {
    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    private CharSequence value;
    private int occurence;

    protected Word(Parcel in) {
        value = in.readString();
        occurence = in.readInt();
    }

    public Word(CharSequence charSequence) {
        value = charSequence;
        occurence = 1;
    }

    public Word(CharSequence charSequence,int repetition) {
        value = charSequence;
        occurence = repetition;
    }


    public void increment(){
        ++occurence;
    }


    public int getOccurence() {
        return occurence;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(String.valueOf(value));
        parcel.writeInt(occurence);
    }

    //TODO generate hashCode
    //TODO generate equals


}
