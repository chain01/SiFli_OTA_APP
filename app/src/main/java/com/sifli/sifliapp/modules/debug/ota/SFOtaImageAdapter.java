package com.sifli.sifliapp.modules.debug.ota;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sifli.sifliapp.R;
import com.sifli.siflicore.log.SFLog;

import java.util.List;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/1/6
 * description
 */
public class SFOtaImageAdapter extends BaseAdapter implements  View.OnClickListener {
    private final static String TAG = "SFOtaImageAdapter";
    private List<SFOtaImageItem> list;
    private Context context = null;
    ISFOtaImageAdapterCallback callback;
    public SFOtaImageAdapter(Context context, List<SFOtaImageItem> list,ISFOtaImageAdapterCallback callback){
        this.context = context;
        this.list = list;
        this.callback = callback;
    }
    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if(convertView == null){
            mHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.row_sf_ota_image_item, null, true);
            mHolder.fileNameTv = (TextView) convertView.findViewById(R.id.sf_ota_image_item_name_tv);
            mHolder.selectTypeBtn = (Button) convertView.findViewById(R.id.sf_ota_image_item_select_btn);
            mHolder.removeBtn = (Button) convertView.findViewById(R.id.sf_ota_image_item_remove_btn);
            mHolder.offsetEt = (EditText)convertView.findViewById(R.id.sf_ota_image_item_offset_et);
            convertView.setTag(mHolder);
            mHolder.selectTypeBtn.setOnClickListener(this);
            mHolder.removeBtn.setOnClickListener(this);
            registerEditTextListener(mHolder.offsetEt);
        }else{
            mHolder = (ViewHolder) convertView.getTag();
        }
        SFOtaImageItem item = list.get(position);
        mHolder.fileNameTv.setText(item.getFileName());
        mHolder.selectTypeBtn.setText(item.getImageIDName());
        mHolder.selectTypeBtn.setTag(item);
        mHolder.removeBtn.setTag(item);
        mHolder.offsetEt.setTag(item);
        mHolder.offsetEt.setText(item.getHexOffset());
        return convertView;
    }

    @Override
    public void onClick(View v) {
        SFLog.i(TAG,"onClick");
        int viewId = v.getId();
        if(viewId == R.id.sf_ota_image_item_select_btn){
            if(callback != null){
                callback.onSelectTypeBtnTouch((SFOtaImageItem)v.getTag(),v);
            }
        }else if(viewId == R.id.sf_ota_image_item_remove_btn){
            if(callback != null){
                callback.onRemoveBtnTouch((SFOtaImageItem)v.getTag(),v);
            }
        }

    }

    //region TextWatcher
    private void registerEditTextListener(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 文本改变前被调用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 文本正在改变时被调用

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 文本改变后被调用
                SFLog.i(TAG,"afterTextChanged " + s.toString());
                SFOtaImageItem item = (SFOtaImageItem)editText.getTag();
                item.setHexOffset(editText.getText().toString());
            }
        });
    }
    //endregion

    class ViewHolder{
        private TextView fileNameTv;
        private Button selectTypeBtn;
        private Button removeBtn;
        private EditText offsetEt;
    }
}
