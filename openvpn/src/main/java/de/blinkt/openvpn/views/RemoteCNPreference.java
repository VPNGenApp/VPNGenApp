/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.DialogPreference;

import de.blinkt.openvpn.R;

public class RemoteCNPreference extends DialogPreference {


    private int mDNType;
    private String mDn;

    public RemoteCNPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public RemoteCNPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RemoteCNPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RemoteCNPreference(Context context) {
        super(context);
    }


    public void setDN(String dn) {
        mDn = dn;
        notifyChanged();
    }


    public void setAuthType(int x509authtype) {
        mDNType = x509authtype;
        notifyChanged();
    }

    public String getCNText() {
        return mDn;
    }

    public int getAuthtype() {
        return mDNType;
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.tlsremote;
    }
}
