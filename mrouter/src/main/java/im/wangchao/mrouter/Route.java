package im.wangchao.mrouter;

import android.net.Uri;
import android.os.Bundle;

import java.util.Set;

/**
 * <p>Description  : Route.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 上午10:41.</p>
 */
public final class Route {

    private final Uri mUri;
    private final Bundle mBundle;
    private final int mFlags;

    private Route(Builder builder){
        mUri = builder.mUri;
        mBundle = builder.mBundle;
        mFlags = builder.mFlags;
    }

    public Uri uri(){
        return mUri;
    }

    public Bundle bundle(){
        return mBundle;
    }

    public int flags(){
        return mFlags;
    }

    public Builder newBuilder(){
        return new Builder(this);
    }

    public static final class Builder {
        Uri mUri;
        Bundle mBundle;
        int mFlags;

        public Builder(){
            mBundle = new Bundle();
            mFlags = -1;
        }

        private Builder(Route route){
            mUri = route.mUri;
            mBundle = route.mBundle;
            mFlags = route.mFlags;
        }

        public Builder uri(String uri){
            mUri = Uri.parse(uri);
            return this;
        }

        public Builder uri(Uri uri){
            mUri = uri;
            return this;
        }

        public Builder bundle(Bundle bundle){
            if (bundle != null){
                mBundle = bundle;
            }
            return this;
        }

        public Builder flags(int flags){
            mFlags = flags;
            return this;
        }

        public Builder addAll(Bundle bundle){
            if (bundle != null){
                mBundle.putAll(bundle);
            }
            return this;
        }

        public Builder addParameter(String key, Bundle value){
            mBundle.putBundle(key, value);
            return this;
        }

        public Builder addParameter(String key, String value){
            mBundle.putString(key, value);
            return this;
        }

        public Builder addParameter(String key, float value){
            mBundle.putFloat(key, value);
            return this;
        }

        public Builder addParameter(String key, int value){
            mBundle.putInt(key, value);
            return this;
        }

        public Builder addParameter(String key, double value){
            mBundle.putDouble(key, value);
            return this;
        }

        public Builder addParameter(String key, long value){
            mBundle.putLong(key, value);
            return this;
        }

        public Route build(){
            if (mUri == null) throw new NullPointerException("Route.Builder mUri can not be null.");
            if (mBundle == null) throw new NullPointerException("Route.Builder mBundle can not be null.");

            Set<String> keys = mUri.getQueryParameterNames();
            for (String key : keys) {
                mBundle.putString(key, mUri.getQueryParameter(key));
            }

            return new Route(this);
        }
    }
}
