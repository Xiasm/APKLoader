package com.xiashengming.plugin.internal;

import android.content.Context;
import android.content.pm.PackageParser;
import android.os.Build;

import com.xiashengming.plugin.utils.RefInvoke;

import java.io.File;

public class PackageParseCompat {
    public static final PackageParser.Package parsePackage(final Context context, final File apk, final int flags) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                return PackageParserV24.parsePackage(context, apk, flags);
            } else if (Build.VERSION.SDK_INT >= 21) {
                return PackageParserLollipop.parsePackage(context, apk, flags);
            } else {
                return PackageParserLegacy.parsePackage(context, apk, flags);
            }

        } catch (Throwable e) {
            throw new RuntimeException("error", e);
        }
    }

    private static final class PackageParserV24 {

        static final PackageParser.Package parsePackage(Context context, File apk, int flags) throws Throwable {
            PackageParser parser = new PackageParser();
            PackageParser.Package pkg = parser.parsePackage(apk, flags);
            RefInvoke.invokeInstanceMethod(parser,
                    "collectCertificates",
                    new Class[]{PackageParser.Package.class, int.class},
                    new Object[]{pkg, flags});
            return pkg;
        }
    }

    private static final class PackageParserLollipop {

        static final PackageParser.Package parsePackage(final Context context, final File apk, final int flags) throws Throwable {
            PackageParser parser = new PackageParser();
            PackageParser.Package pkg = parser.parsePackage(apk, flags);
            parser.collectCertificates(pkg, flags);
            return pkg;
        }

    }

    private static final class PackageParserLegacy {

        static final PackageParser.Package parsePackage(Context context, File apk, int flags) throws Throwable {
            PackageParser parser = new PackageParser(apk.getAbsolutePath());
            PackageParser.Package pkg = parser.parsePackage(apk, apk.getAbsolutePath(), context.getResources().getDisplayMetrics(), flags);
            RefInvoke.invokeInstanceMethod(parser,
                    "collectCertificates",
                    new Class[]{PackageParser.Package.class, int.class},
                    new Object[]{pkg, flags});
            return pkg;
        }

    }
}
