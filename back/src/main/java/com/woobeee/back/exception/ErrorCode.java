package com.woobeee.back.exception;

public enum ErrorCode {
    signIn_GoogleTokenNotValid,
    signIn_userConflict,
    login_userNotFound,
    login_passwordNotMatch,

    comment_needAuthentication,
    like_needAuthentication,
    post_notFound
}
