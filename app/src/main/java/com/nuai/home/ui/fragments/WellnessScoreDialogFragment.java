package com.nuai.home.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nuai.R;
import com.nuai.databinding.WellnessScoreBottomSheetBinding;
import com.nuai.onboarding.ui.activity.WebActivity;
import com.nuai.utils.AppConstant;
import com.nuai.utils.CommonUtils;
import com.nuai.utils.CustomTypefaceSpan;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WellnessScoreDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private WellnessScoreBottomSheetBinding binding;
    private int wellnessScore = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.wellness_score_bottom_sheet, container, false
        );
        init();
        return binding.getRoot();

    }

    private void init() {
        String fullMessage = String.format(getString(R.string.wellness_score_msg_2), AppConstant.BINAH_AI_URL);
        setSpannableColor(binding.tvMsg2,
                fullMessage,
                AppConstant.BINAH_AI_URL,
                ContextCompat.getColor(requireActivity(), R.color.blue_text_color));
        setWellnessScore();
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void setWellnessScore() {
        binding.progressBar.setOnTouchListener((v, event) -> true);
        binding.tvWellnessScore.setText(wellnessScore + "/10 " +
                CommonUtils.INSTANCE.getWellnessLevel(requireActivity(), wellnessScore));
        binding.progressBar.setMin(1);
        binding.progressBar.setMax(10);
        binding.progressBar.setProgress(wellnessScore);

    }

    private void setSpannableColor(
            TextView view, String fulltext, String subtext1, int color
    ) {
        Typeface regular = ResourcesCompat.getFont(requireActivity(), R.font.switzer_regular);
        Spannable str = new SpannableString(fulltext);
        int i1 = fulltext.indexOf(subtext1);
        str.setSpan(
                new CustomTypefaceSpan("", regular), i1, i1 + subtext1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        str.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                WebActivity.Companion.startActivity(
                        requireActivity(),
                        getString(R.string.app_name),
                        subtext1
                );
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, i1, i1 + subtext1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(
                new ForegroundColorSpan(color), i1, i1 + subtext1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(str);
        view.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog1 = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog1.setOnShowListener(dialog -> {
            View bottomSheet = (FrameLayout) dialog1.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog1;
    }

    public static String TAG = WellnessScoreDialogFragment.class.getSimpleName();

    public static WellnessScoreDialogFragment onNewInstance(
            int wellnessScore, boolean cancellable
    ) {
        WellnessScoreDialogFragment fragment = new WellnessScoreDialogFragment();
        fragment.wellnessScore = wellnessScore;
        fragment.setCancelable(cancellable);
        return fragment;
    }
}
