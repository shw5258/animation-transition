package com.teamtreehouse.albumcover;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";

    @Bind(R.id.album_art) ImageView albumArtView;
    @Bind(R.id.fab) ImageButton fab;
    @Bind(R.id.title_panel) ViewGroup titlePanel;
    @Bind(R.id.track_panel) ViewGroup trackPanel;
    @Bind(R.id.detail_container) ViewGroup detailContainer;

    private TransitionManager mTransitionManager;
    private Scene mExpandedScene;
    private Scene mCollapsedScene;
    private Scene mCurrentScene;

    //onVHClicked(AlbumVH vh)에 의해서 이 액티비티의 onCreate메서드가 호출되는 것 같습니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
        setupTransitions();
    }

    private void animate() {
//        ObjectAnimator scalex = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1);
//        ObjectAnimator scaley = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);
//        AnimatorSet scaleFab = new AnimatorSet();
//        scaleFab.playTogether(scalex, scaley);
        Animator scaleFab = AnimatorInflater.loadAnimator(this, R.animator.scale);
        scaleFab.setTarget(fab);

        int titleStartValue = titlePanel.getTop();
        int titleEndValue = titlePanel.getBottom();
        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(titlePanel, "bottom", titleStartValue, titleEndValue);
        animatorTitle.setInterpolator(new AccelerateInterpolator());

        int trackStartValue = trackPanel.getTop();
        int trackEndValue = trackPanel.getBottom();
        ObjectAnimator animatorTrack = ObjectAnimator.ofInt(trackPanel, "bottom", trackStartValue, trackEndValue);
        animatorTrack.setInterpolator(new DecelerateInterpolator());

        titlePanel.setBottom(titleStartValue);
        trackPanel.setBottom(titleStartValue);
        fab.setScaleX(0);
        fab.setScaleY(0);

//        animatorTitle.setDuration(1000);
//        animatorTrack.setDuration(1000);
//        animatorTitle.setStartDelay(1000);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animatorTitle, animatorTrack, scaleFab);
        set.start();
    }

    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {
        animate();
    }

    @OnClick(R.id.track_panel)
    public void onTrackPanelClicked(View view) {
        if (mCurrentScene == mExpandedScene) {
            mCurrentScene = mCollapsedScene;
        }
        else {
            mCurrentScene = mExpandedScene;
        }
        mTransitionManager.transitionTo(mCurrentScene);
    }

    private void setupTransitions() {
        mTransitionManager = new TransitionManager();
        ViewGroup transitionRoot = detailContainer;

        // Expanded scene
        mExpandedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail_expanded, this);

        mExpandedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mExpandedScene;
            }
        });

        TransitionSet expandTransitionSet = new TransitionSet();
        /*
        <transitionSet xmlns:android="http://schemas.android.com/apk/res/android"
                 android:ordering="sequential">
             <fade/>
             <changeBounds/>
         </transitionSet>
         저번에 트랜지션이 xml로 안된다고 했는데 지금 보니 document에는 이렇게
         순서를 지정하는 것도 가능할 것으로 나옵니다.
         */
        expandTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200);
        expandTransitionSet.addTransition(changeBounds);

        Fade fadeLyrics = new Fade();
        fadeLyrics.addTarget(R.id.lyrics);
        fadeLyrics.setDuration(150);
        expandTransitionSet.addTransition(fadeLyrics);
        //Fade 객체는 처음에는 숨겨진상태로 존재하는 듯 합니다.
        //위에 적힌바와 같이 expandTransitionSet에 add 된 순서대로 트랜지션이 진행됩니다.
        //이 장면은 가사가 나타나는 장면입니다.

        // Collapsed scene
        mCollapsedScene = Scene.getSceneForLayout(transitionRoot,
                R.layout.activity_album_detail, this);

        mCollapsedScene.setEnterAction(new Runnable() {
            @Override
            public void run() {
                ButterKnife.bind(AlbumDetailActivity.this);
                populate();
                mCurrentScene = mCollapsedScene;
            }
        });

        TransitionSet collapseTransitionSet = new TransitionSet();
        collapseTransitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        Fade fadeOutLyrics = new Fade();
        fadeOutLyrics.addTarget(R.id.lyrics);
        fadeOutLyrics.setDuration(150);
        collapseTransitionSet.addTransition(fadeOutLyrics);

        ChangeBounds resetBounds = new ChangeBounds();
        resetBounds.setDuration(200);
        collapseTransitionSet.addTransition(resetBounds);
        //가사가 접히는 장면입니다.

        mTransitionManager.setTransition(mExpandedScene, mCollapsedScene, collapseTransitionSet);
        mTransitionManager.setTransition(mCollapsedScene, mExpandedScene, expandTransitionSet);
        //setupTransition()은 onCreate()단계에서 실행되므로 준비단계입니다.
        //그러므로 가사가 닫힌상태로 시작시킵니다.
        mCollapsedScene.enter();
    }

    private void populate() {
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);
        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);
    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        // reduce image size in memory to avoid memory errors
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        //샘플크기를 8분의1로 합니다.
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }

    private void colorizeFromImage(Bitmap image) {
        Palette palette = Palette.from(image).generate();

        // set panel colors
        int defaultPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;
        //뒤쪽에 get메서드가 사진의 전체적으로 어울리는 색상을 골라주는 메서드입니다.
        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultPanelColor));

        // 둥근버튼의 색상을 설정합니다.
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaultFabColor)
        };
        fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
