package com.teamtreehouse.albumcover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumListActivity extends Activity {
    //리사이클러뷰는 뷰안에 있는 뷰나 뷰그룹들을 스크롤할수 있게 해줍니다.
    @Bind(R.id.album_list) RecyclerView mAlbumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        initTransitions();

        //어노테이드 되는 필드와 메서드들을 붙여줍니다. 현재의 컨텐트 뷰가 루트뷰로 사용됩니다.
        ButterKnife.bind(this);
        populate();
    }

    private void initTransitions() {
        //getWindow()는 Activity의 method이며 Activity는 Window Class를 import합니다.
        //setExit과 setReenter 트랜지션은 이 장면이 오고갈때 진행되는 트랜지션을 설정합니다.
        getWindow().setExitTransition(null);
        getWindow().setReenterTransition(null);
    }

    interface OnVHClickedListener {
        void onVHClicked(AlbumVH vh);
    }

    static class AlbumVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnVHClickedListener mListener;
        @Bind(R.id.album_art)
        ImageView albumArt;

        public AlbumVH(View itemView, OnVHClickedListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.onVHClicked(this);
        }
    }

    private void populate() {
        //레이아웃메니저로 사진목록이 수직으로 나열될것인지 아니면 수평으로 나열될 것인지를 결정합니다.
        //그리고 그 폭을 사진 몇개로 할것인지를 정합니다.
        //StaggeredGridLayoutManager와 GridLayoutManager중에 왜 이것을 선택했는지는 아직 모르겠습니다.
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        //리사이클뷰안에 하위뷰를 세팅합니다.
        mAlbumList.setLayoutManager(lm);
        //사진목록을 작성합니다.
        final int[] albumArts = {
                R.drawable.mean_something_kinder_than_wolves,
                R.drawable.cylinders_chris_zabriskie,
                R.drawable.broken_distance_sutro,
                R.drawable.playing_with_scratches_ruckus_roboticus,
                R.drawable.keep_it_together_guster,
                R.drawable.the_carpenter_avett_brothers,
                R.drawable.please_sondre_lerche,
                R.drawable.direct_to_video_chris_zabriskie };

        RecyclerView.Adapter adapter = new RecyclerView.Adapter<AlbumVH>() {
            @Override
            public AlbumVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View albumView = getLayoutInflater().inflate(R.layout.album_grid_item, parent, false);
                //앨범뷰홀더에 앨범어래이를 집어넣고 각 앨범이 클릭될때 클린된 앨범의정보를 파악하고
                //다음 상세뷰에 전달해주는 역할을 하는 것 같습니다.
                return new AlbumVH(albumView, new OnVHClickedListener() {
                    @Override
                    public void onVHClicked(AlbumVH vh) {
                        int albumArtResId = albumArts[vh.getLayoutPosition() % albumArts.length];
                        //제 생각에는 앨범목록뷰에서 앨범상세뷰로 가는 인텐트를 생성합니다.
                        Intent intent = new Intent(AlbumListActivity.this, AlbumDetailActivity.class);
                        //클릭된 앨범주소에 앨범이 없을 경우 디폴트 앨범을 지정해주는 것 같습니다.
                        intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_ART_RESID, albumArtResId);
                        //인텐트를 실행하도록 명령합니다.
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onBindViewHolder(AlbumVH holder, int position) {
                holder.albumArt.setImageResource(albumArts[position % albumArts.length]);
            }
            //기존의 앨범들을 곱배기해서 중복시킵니다.
            @Override
            public int getItemCount() {
                return albumArts.length * 4;
            }

        };
        //뷰에 뿌려질 데이터폼을 만들어주는 어댑터를 붙여줍니다.
        mAlbumList.setAdapter(adapter);
    }

}
