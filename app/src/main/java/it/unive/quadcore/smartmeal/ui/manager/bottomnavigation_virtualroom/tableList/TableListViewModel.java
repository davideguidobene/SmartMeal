package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom.tableList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TableListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TableListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}