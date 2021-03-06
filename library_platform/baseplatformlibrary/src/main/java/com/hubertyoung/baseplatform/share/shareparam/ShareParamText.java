/*
 * Copyright (C) 2015 Bilibili <jungly.ik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hubertyoung.baseplatform.share.shareparam;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <br>
 * function:
 * <p>
 *
 * @author:HubertYoung
 * @date:2018/9/14 09:40
 * @since:V1.0.0
 * @desc:com.hubertyoung.baseplatform.share.shareparam
 */
public class ShareParamText extends BaseShareParam {

    public ShareParamText( String title, String content) {
        super(title, content);
    }

    public ShareParamText( String title, String content, String targetUrl) {
        super(title, content, targetUrl);
    }

    @Override
    public void writeToParcel( Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected ShareParamText(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<ShareParamText> CREATOR = new Parcelable.Creator<ShareParamText>() {
        public ShareParamText createFromParcel(Parcel source) {
            return new ShareParamText(source);
        }

        public ShareParamText[] newArray(int size) {
            return new ShareParamText[size];
        }
    };
}
