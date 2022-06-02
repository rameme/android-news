// Generated by view binder compiler. Do not edit!
package com.example.androidnews.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.androidnews.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityHeadlineBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Spinner categoryHeadline;

  @NonNull
  public final TextView categoryHeadlineTitle;

  @NonNull
  public final TextView headline;

  @NonNull
  public final RecyclerView headlineRecyclerView;

  @NonNull
  public final Button nextButton;

  @NonNull
  public final TextView pages;

  @NonNull
  public final Button previousButton;

  @NonNull
  public final ProgressBar progressHeadlineBar;

  private ActivityHeadlineBinding(@NonNull ConstraintLayout rootView,
      @NonNull Spinner categoryHeadline, @NonNull TextView categoryHeadlineTitle,
      @NonNull TextView headline, @NonNull RecyclerView headlineRecyclerView,
      @NonNull Button nextButton, @NonNull TextView pages, @NonNull Button previousButton,
      @NonNull ProgressBar progressHeadlineBar) {
    this.rootView = rootView;
    this.categoryHeadline = categoryHeadline;
    this.categoryHeadlineTitle = categoryHeadlineTitle;
    this.headline = headline;
    this.headlineRecyclerView = headlineRecyclerView;
    this.nextButton = nextButton;
    this.pages = pages;
    this.previousButton = previousButton;
    this.progressHeadlineBar = progressHeadlineBar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityHeadlineBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityHeadlineBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_headline, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityHeadlineBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.categoryHeadline;
      Spinner categoryHeadline = ViewBindings.findChildViewById(rootView, id);
      if (categoryHeadline == null) {
        break missingId;
      }

      id = R.id.categoryHeadlineTitle;
      TextView categoryHeadlineTitle = ViewBindings.findChildViewById(rootView, id);
      if (categoryHeadlineTitle == null) {
        break missingId;
      }

      id = R.id.headline;
      TextView headline = ViewBindings.findChildViewById(rootView, id);
      if (headline == null) {
        break missingId;
      }

      id = R.id.headlineRecyclerView;
      RecyclerView headlineRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (headlineRecyclerView == null) {
        break missingId;
      }

      id = R.id.nextButton;
      Button nextButton = ViewBindings.findChildViewById(rootView, id);
      if (nextButton == null) {
        break missingId;
      }

      id = R.id.pages;
      TextView pages = ViewBindings.findChildViewById(rootView, id);
      if (pages == null) {
        break missingId;
      }

      id = R.id.previousButton;
      Button previousButton = ViewBindings.findChildViewById(rootView, id);
      if (previousButton == null) {
        break missingId;
      }

      id = R.id.progressHeadlineBar;
      ProgressBar progressHeadlineBar = ViewBindings.findChildViewById(rootView, id);
      if (progressHeadlineBar == null) {
        break missingId;
      }

      return new ActivityHeadlineBinding((ConstraintLayout) rootView, categoryHeadline,
          categoryHeadlineTitle, headline, headlineRecyclerView, nextButton, pages, previousButton,
          progressHeadlineBar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
