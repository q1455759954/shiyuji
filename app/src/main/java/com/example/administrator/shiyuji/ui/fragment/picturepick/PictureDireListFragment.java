package com.example.administrator.shiyuji.ui.fragment.picturepick;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.AListFragment;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.download.SdcardDownloader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.utils.SystemUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019/8/3.
 */

public class PictureDireListFragment extends AListFragment<PictureDireListFragment.PictureFileDire, ArrayList<PictureDireListFragment.PictureFileDire>, PictureDireListFragment.PictureFileDire>
        implements AdapterView.OnItemClickListener {

    public static PictureDireListFragment newInstance(String currentDire, ArrayList<PictureFileDire> files) {
        Bundle args = new Bundle();
        args.putSerializable("files", files);
        args.putSerializable("current", currentDire);

        PictureDireListFragment fragment = new PictureDireListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<PictureFileDire> files;
    private String currentDire;
    private OnPictureDireSelectedCallback callback;

    @Override
    public int inflateContentView() {
        return R.layout.ui_picture_pick_dire;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        files = savedInstanceState == null ? (ArrayList<PictureFileDire>) getArguments().getSerializable("files")
                : (ArrayList<PictureFileDire>) savedInstanceState.getSerializable("files");
        currentDire = savedInstanceState == null ? getArguments().getString("current")
                : savedInstanceState.getString("current");
    }

    @Override
    protected void setupRefreshConfig(RefreshConfig config) {
        super.setupRefreshConfig(config);

        config.footerMoreEnable = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (callback != null) {
            PictureFileDire dire = getAdapterItems().get(position);

            callback.onPictureDireSelected(dire);

            currentDire = dire.getName();
            getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("files", files);
        outState.putSerializable("current", currentDire);
    }

    @Override
    public IItemViewCreator<PictureFileDire> configItemViewCreator() {
        return new IItemViewCreator<PictureFileDire>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(R.layout.item_picture_pick_dire, parent, false);
            }

            @Override
            public IITemView<PictureFileDire> newItemView(View convertView, int viewType) {
                return new PictureDireItemView(convertView);
            }

        };
    }

    @Override
    public void requestData(RefreshMode mode) {
        setItems(files);
    }

    class PictureDireItemView extends ARecycleViewItemView<PictureFileDire> {

        @ViewInject(id = R.id.img)
        ImageView img;
        @ViewInject(id = R.id.txtName)
        TextView txtName;
        @ViewInject(id = R.id.txtDesc)
        TextView txtDesc;
        @ViewInject(id = R.id.imgSelected)
        View imgSelected;

        public PictureDireItemView(View itemView) {
            super(getActivity(), itemView);
        }

        @Override
        public void onBindData(View convertView, PictureFileDire data, int position) {
            ImageConfig config = new ImageConfig();
            config.setLoadfaildRes(R.drawable.bg_timeline_loading);
            config.setLoadingRes(R.drawable.bg_timeline_loading);
            config.setMaxWidth(SystemUtils.getScreenWidth(getActivity()) / 3);
            config.setMaxHeight(SystemUtils.getScreenWidth(getActivity()) / 3);
            config.setCacheEnable(false);
            config.setDownloaderClass(SdcardDownloader.class);
            config.setId("thumb_dir");
            BitmapLoader.getInstance().display(PictureDireListFragment.this, data.getFiles().get(0), img, config);

            txtName.setText(data.getName());
            txtDesc.setText(data.getFiles().size() == 0 ? "" : String.format("%d张", data.getFiles().size()));

            imgSelected.setVisibility(currentDire.equals(data.getName()) ? View.VISIBLE : View.GONE);
        }

    }

    public static class PictureFileDire implements Serializable {

        private static final long serialVersionUID = 1746378490588970508L;

        private String name;

        private ArrayList<String> files;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<String> getFiles() {
            return files;
        }

        public void setFiles(ArrayList<String> files) {
            this.files = files;
        }
    }

    public void setCallback(OnPictureDireSelectedCallback callback) {
        this.callback = callback;
    }

    public interface OnPictureDireSelectedCallback {

        public void onPictureDireSelected(PictureFileDire dire);

    }

}
