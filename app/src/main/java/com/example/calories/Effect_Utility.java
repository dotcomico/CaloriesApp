package com.example.calories;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class Effect_Utility {

    //  !השורה להקלדה בקוד עצמו
    //       changeImageWithLiveEffect(iv_backToMain, R.drawable.ic_baseline_arrow_circle_right_blue);


    //  אהבתי :  // changeImageWithBounce  , changeImageWithDynamicRotation , changeImageWithMirrorEffect


    public  static void changeImageWithColorExplosion(ImageView imageView, int newImageRes) {
        imageView.setImageResource(newImageRes); // החלפת תמונה
        imageView.setAlpha(0f); // התמונה מתחילה לא שקופה
        imageView.animate().alpha(1f).setDuration(300); // התמונה מתפשטת ונהפכת לשקופה
        // תוכל להוסיף כאן גם אפקטים צבעוניים נוספים כמו שינוי צבע של התמונה
    }


    public  static void changeImageWithDynamicRotation(ImageView imageView, int newImageRes) {
        imageView.animate().rotationBy(720f).setDuration(500) // סיבוב של 720 מעלות (2 סיבובים)
                .withEndAction(() -> imageView.setImageResource(newImageRes)); // החלפת תמונה בסוף האנימציה
    }

    public  static void changeImageWithMirrorEffect(ImageView imageView, int newImageRes) {
        imageView.setImageResource(newImageRes); // החלפת תמונה
        imageView.setScaleX(-1f); // הופך את התמונה כמו מראה
        imageView.animate().scaleX(1f).setDuration(300); // התמונה מתהפכת חזרה
    }


    public  static void changeImageWithLiveEffect(ImageView imageView, int newImageRes) {
        imageView.setImageResource(newImageRes); // החלפת תמונה
        imageView.setScaleX(0.5f); // התמונה מתחילה מוקטנת
        imageView.setScaleY(0.5f);
        imageView.animate().scaleX(1f).scaleY(1f).setDuration(500); // התמונה מתפשטת ותופס את המידות הרגילות
    }

    public  static void changeImageWithBlackout(ImageView imageView, int newImageRes) {
        imageView.setAlpha(1f); // התמונה מתחילה נראית
        imageView.animate().alpha(0f).setDuration(300) // מתפוגגת לאט
                .withEndAction(() -> {
                    imageView.setImageResource(newImageRes); // החלפת תמונה
                    imageView.setAlpha(0f); // התמונה נשארת לא שקופה
                    imageView.animate().alpha(1f).setDuration(300); // התמונה מתפוגגת בחזרה
                });
    }


    public  static void changeImageWithBounce(ImageView imageView, int newImageRes) {
        imageView.setImageResource(newImageRes); // החלפת תמונה
        imageView.setScaleX(0.5f); // התמונה מתחילה מוקטנת
        imageView.setScaleY(0.5f);
        imageView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300) // התמונה קופצת
                .withEndAction(() -> imageView.animate().scaleX(1f).scaleY(1f).setDuration(100)); // התמונה מתייצבת במקום
    }


    public  static void changeImageWithShatter(ImageView imageView, int newImageRes) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setImageResource(newImageRes); // החלפת תמונה
                imageView.setScaleX(0f); // מיקום התמונה
                imageView.setScaleY(0f);
                imageView.animate().scaleX(1f).scaleY(1f).setDuration(300); // התמונה חוזרת לגודל המקורי
            }
        });
        animatorSet.start();
    }

    public  static void changeImageWithDive(ImageView imageView, int newImageRes) {
        ObjectAnimator diveDown = ObjectAnimator.ofFloat(imageView, "translationY", 0f, imageView.getHeight());
        diveDown.setDuration(300);

        diveDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setImageResource(newImageRes); // החלפת תמונה
                imageView.setTranslationY(-imageView.getHeight()); // מציב את התמונה מחוץ למסך
                imageView.animate().translationY(0f).setDuration(300); // אנימציה של צלילה חזרה
            }
        });
        diveDown.start();
    }


    public  static void changeImageWithMorphing(ImageView imageView, int newImageRes) {
        imageView.setImageResource(newImageRes);
        imageView.setScaleX(0f); // התמונה מתחילה מ-0
        imageView.setScaleY(0f);
        imageView.animate().scaleX(1f).scaleY(1f).setDuration(500); // התמונה מתפשטת ונהפכת לגודל המקורי
    }

    public  static void changeImageWithShadowEffect(ImageView imageView, int newImageRes) {
        imageView.setImageResource(newImageRes);
        imageView.setAlpha(0f); // התמונה מתחילה לא שקופה
        imageView.animate().alpha(1f).setDuration(400); // התמונה הופכת לשקופה ומופיעה שוב
    }

    public  static void changeImageWithFade(ImageView imageView, int newImageRes) {
        imageView.animate().alpha(0f).setDuration(100).withEndAction(() -> {
            imageView.setImageResource(newImageRes); // החלפת תמונה
            imageView.animate().alpha(1f).setDuration(300); // הופעה הדרגתית
        });
    }

    public  static void crossfadeImage(ImageView imageView, int newImageRes) {
        Drawable newDrawable = ContextCompat.getDrawable(imageView.getContext(), newImageRes);
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                imageView.getDrawable(), // התמונה הנוכחית
                newDrawable              // התמונה החדשה
        });

        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(300); // אנימציה של 500ms
    }
}
