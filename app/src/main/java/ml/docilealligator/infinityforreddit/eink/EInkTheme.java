package ml.docilealligator.infinityforreddit.eink;

import android.content.Context;
import android.graphics.Color;

import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.customtheme.CustomTheme;

/**
 * High-contrast light theme for e-ink displays: black text on pure white, no tinted backgrounds or translucent
 * greys (which render as muddy, ghosting-prone tones), and dark, saturated accents that stay readable on color e-ink
 * (e.g. Kaleido) panels and remain distinguishable in black-and-white mode.
 */
public final class EInkTheme {
    private static final String WHITE = "#FFFFFF";
    private static final String BLACK = "#000000";
    private static final String DARK_GREY = "#333333";
    private static final String MID_GREY = "#666666";
    private static final String LIGHT_GREY = "#E0E0E0";
    private static final String RED = "#C62828";
    private static final String ORANGE = "#D84315";
    private static final String BLUE = "#1565C0";
    private static final String GREEN = "#2E7D32";
    private static final String PURPLE = "#6A1B9A";
    private static final String TEAL = "#00838F";

    private EInkTheme() {
    }

    public static CustomTheme create(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_e_ink));
        customTheme.isLightTheme = true;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor(WHITE);
        customTheme.colorPrimaryDark = Color.parseColor(WHITE);
        customTheme.colorAccent = Color.parseColor(BLACK);
        customTheme.colorPrimaryLightTheme = Color.parseColor(BLACK);
        customTheme.primaryTextColor = Color.parseColor(BLACK);
        customTheme.secondaryTextColor = Color.parseColor(DARK_GREY);
        customTheme.postTitleColor = Color.parseColor(BLACK);
        customTheme.postContentColor = Color.parseColor(BLACK);
        customTheme.readPostTitleColor = Color.parseColor(MID_GREY);
        customTheme.readPostContentColor = Color.parseColor(MID_GREY);
        customTheme.commentColor = Color.parseColor(BLACK);
        customTheme.buttonTextColor = Color.parseColor(WHITE);
        customTheme.backgroundColor = Color.parseColor(WHITE);
        customTheme.cardViewBackgroundColor = Color.parseColor(WHITE);
        customTheme.readPostCardViewBackgroundColor = Color.parseColor(WHITE);
        customTheme.filledCardViewBackgroundColor = Color.parseColor(WHITE);
        customTheme.readPostFilledCardViewBackgroundColor = Color.parseColor(WHITE);
        customTheme.commentBackgroundColor = Color.parseColor(WHITE);
        customTheme.bottomAppBarBackgroundColor = Color.parseColor(WHITE);
        customTheme.primaryIconColor = Color.parseColor(BLACK);
        customTheme.bottomAppBarIconColor = Color.parseColor(BLACK);
        customTheme.postIconAndInfoColor = Color.parseColor(DARK_GREY);
        customTheme.commentIconAndInfoColor = Color.parseColor(DARK_GREY);
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor(BLACK);
        customTheme.toolbarSecondaryTextColor = Color.parseColor(DARK_GREY);
        customTheme.circularProgressBarBackground = Color.parseColor(WHITE);
        customTheme.mediaIndicatorIconColor = Color.parseColor(WHITE);
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor(BLACK);
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor(WHITE);
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor(BLACK);
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor(BLACK);
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor(WHITE);
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor(BLACK);
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor(BLACK);
        customTheme.upvoted = Color.parseColor(ORANGE);
        customTheme.downvoted = Color.parseColor(BLUE);
        customTheme.postTypeBackgroundColor = Color.parseColor(BLACK);
        customTheme.postTypeTextColor = Color.parseColor(WHITE);
        customTheme.spoilerBackgroundColor = Color.parseColor(PURPLE);
        customTheme.spoilerTextColor = Color.parseColor(WHITE);
        customTheme.nsfwBackgroundColor = Color.parseColor(RED);
        customTheme.nsfwTextColor = Color.parseColor(WHITE);
        customTheme.flairBackgroundColor = Color.parseColor(GREEN);
        customTheme.flairTextColor = Color.parseColor(WHITE);
        customTheme.awardsBackgroundColor = Color.parseColor(ORANGE);
        customTheme.awardsTextColor = Color.parseColor(WHITE);
        customTheme.archivedTint = Color.parseColor(PURPLE);
        customTheme.lockedIconTint = Color.parseColor(ORANGE);
        customTheme.crosspostIconTint = Color.parseColor(RED);
        customTheme.upvoteRatioIconTint = Color.parseColor(BLUE);
        customTheme.stickiedPostIconTint = Color.parseColor(GREEN);
        customTheme.noPreviewPostTypeIconTint = Color.parseColor(WHITE);
        customTheme.subscribed = Color.parseColor(RED);
        customTheme.unsubscribed = Color.parseColor(BLUE);
        customTheme.username = Color.parseColor(BLUE);
        customTheme.subreddit = Color.parseColor(RED);
        customTheme.authorFlairTextColor = Color.parseColor(PURPLE);
        customTheme.submitter = Color.parseColor(ORANGE);
        customTheme.moderator = Color.parseColor(GREEN);
        customTheme.currentUser = Color.parseColor(TEAL);
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor(LIGHT_GREY);
        customTheme.unreadMessageBackgroundColor = Color.parseColor(LIGHT_GREY);
        customTheme.dividerColor = Color.parseColor(MID_GREY);
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor(BLACK);
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#BDBDBD");
        customTheme.commentVerticalBarColor1 = Color.parseColor(BLACK);
        customTheme.commentVerticalBarColor2 = Color.parseColor(BLUE);
        customTheme.commentVerticalBarColor3 = Color.parseColor(RED);
        customTheme.commentVerticalBarColor4 = Color.parseColor(GREEN);
        customTheme.commentVerticalBarColor5 = Color.parseColor(PURPLE);
        customTheme.commentVerticalBarColor6 = Color.parseColor(ORANGE);
        customTheme.commentVerticalBarColor7 = Color.parseColor(TEAL);
        customTheme.fabIconColor = Color.parseColor(WHITE);
        customTheme.chipTextColor = Color.parseColor(WHITE);
        customTheme.linkColor = Color.parseColor(BLUE);
        customTheme.receivedMessageTextColor = Color.parseColor(BLACK);
        customTheme.sentMessageTextColor = Color.parseColor(WHITE);
        customTheme.receivedMessageBackgroundColor = Color.parseColor(LIGHT_GREY);
        customTheme.sentMessageBackgroundColor = Color.parseColor(BLACK);
        customTheme.sendMessageIconColor = Color.parseColor(BLACK);
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor(LIGHT_GREY);
        customTheme.awardedCommentBackgroundColor = Color.parseColor(WHITE);
        customTheme.navBarColor = Color.parseColor(WHITE);
        customTheme.isLightStatusBar = true;
        customTheme.isLightNavBar = true;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }
}
