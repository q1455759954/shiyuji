package com.example.administrator.shiyuji.ui.fragment.publish;

/**
 * Created by Administrator on 2019/7/7.
 */

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.support.bean.Emotion;
import com.example.administrator.shiyuji.support.bean.Emotions;
import com.example.administrator.shiyuji.support.sqlit.EmotionsDB;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情窗口
 *
 *
 */
public class EmotionFragment extends AGridFragment<Emotion, Emotions, Emotion>
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static EmotionFragment newInstance() {
        return new EmotionFragment();
    }

    private OnEmotionSelectedListener onEmotionSelectedListener;

    @Override
    public int inflateContentView() {
        return R.layout.ui_emotions;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

        getRefreshView().setOnItemClickListener(this);
        getRefreshView().setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onEmotionSelectedListener != null)
            onEmotionSelectedListener.onEmotionSelected(getAdapterItems().get(position));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showMessage(getAdapterItems().get(position).getKey());
        return true;
    }

    @Override
    public IItemViewCreator<Emotion> configItemViewCreator() {
        return new IItemViewCreator<Emotion>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(R.layout.item_emotion, parent, false);
            }

            @Override
            public IITemView<Emotion> newItemView(View convertView, int viewType) {
                return new EmotionItemView(convertView);
            }

        };
    }

    @Override
    public void requestData(RefreshMode mode) {
        // 浪小花-lxh_
        new EmotionTask(mode).execute("d_");
    }

    class EmotionItemView extends ARecycleViewItemView<Emotion> {

        @ViewInject(id = R.id.imgEmotion)
        ImageView imgEmotion;

        public EmotionItemView(View itemView) {
            super(getActivity(), itemView);
        }

        @Override
        public void onBindData(View convertView, Emotion data, int position) {
            imgEmotion.setImageBitmap(BitmapFactory.decodeByteArray(data.getData(), 0, data.getData().length));
        }

    }

    class EmotionTask extends APagingTask<String, Void, Emotions> {

        public EmotionTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<Emotion> parseResult(Emotions result) {
            return result.getEmotions();
        }

        @Override
        protected Emotions workInBackground(RefreshMode mode, String previousPage, String nextPage, String... params) throws TaskException {
//			return EmotionsDB.getEmotions(params[0]);
            Emotions es = new Emotions();
            es.setEmotions(new ArrayList<Emotion>());

            Emotions emotions = EmotionsDB.getEmotions("d_");
            es.getEmotions().addAll(emotions.getEmotions());
            emotions = EmotionsDB.getEmotions("hs_");
            es.getEmotions().addAll(emotions.getEmotions());
            emotions = EmotionsDB.getEmotions("h_");
            es.getEmotions().addAll(emotions.getEmotions());
            emotions = EmotionsDB.getEmotions("f_");
            es.getEmotions().addAll(emotions.getEmotions());
            emotions = EmotionsDB.getEmotions("o_");
            es.getEmotions().addAll(emotions.getEmotions());
            emotions = EmotionsDB.getEmotions("w_");
            es.getEmotions().addAll(emotions.getEmotions());
            emotions = EmotionsDB.getEmotions("l_");
            es.getEmotions().addAll(emotions.getEmotions());
//			emotions = EmotionsDB.getEmotions("lxh_");
//			es.getEmotions().addAll(emotions.getEmotions());

            return es;
        }

    }

    public void setOnEmotionListener(OnEmotionSelectedListener onEmotionSelectedListener) {
        this.onEmotionSelectedListener = onEmotionSelectedListener;
    }

    public interface OnEmotionSelectedListener {

        public void onEmotionSelected(Emotion emotion);

    }

}
