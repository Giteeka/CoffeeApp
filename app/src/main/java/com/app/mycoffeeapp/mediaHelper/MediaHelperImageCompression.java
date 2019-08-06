package com.app.mycoffeeapp.mediaHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.app.mycoffeeapp.BuildConfig;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Media FileDirectoryUtils : Helps developer to take picture from gallery, camera, video from gallery and
 * load them in image view If it is video thumbnail is shown on it.
 *
 * @author Dipen Jansari
 */
public class MediaHelperImageCompression {

    private static final String TAG = MediaHelper.class.getName();
    private static final float MAX_HEIGHT = 1280.0f;
    private static final float MAX_WIDTH = 1280.0f;
    private int currentCodeToSend = 100;
    private HashMap<Integer, MediaCallback> mediaHolderList;
    private Media mMediaType;
    private Activity mActivity;
    private Fragment mFragment;


    public MediaHelperImageCompression(Fragment fragment, Media type) {
        mFragment = fragment;
        mediaHolderList = new HashMap<>();
    }

    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private static Bitmap getBitmapFromFile(File file) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(file.getPath(), bmOptions); //it will fill
        // bmOptions with image
        // height & width.
        int actualHeight = bmOptions.outHeight;
        int actualWidth = bmOptions.outWidth;
        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = MAX_WIDTH / MAX_HEIGHT;
        if (actualHeight > MAX_HEIGHT || actualWidth > MAX_WIDTH) {
            if (imgRatio < maxRatio) {
                imgRatio = MAX_HEIGHT / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) MAX_HEIGHT;
            } else if (imgRatio > maxRatio) {
                imgRatio = MAX_WIDTH / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) MAX_WIDTH;
            } else {
                actualHeight = (int) MAX_HEIGHT;
                actualWidth = (int) MAX_WIDTH;
            }
        }
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, actualWidth, actualHeight);
        // Decode bitmap with inSampleSize set
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inDither = false;
        bmOptions.inPurgeable = true;
        bmOptions.inInputShareable = true;
        bmOptions.inTempStorage = new byte[16 * 1024];
        try {
            bmp = BitmapFactory.decodeFile(file.getPath(), bmOptions);
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        }
        float ratioX = actualWidth / (float) bmOptions.outWidth;
        float ratioY = actualHeight / (float) bmOptions.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new
                Paint(Paint.FILTER_BITMAP_FLAG));
        if (bmp != null) {
            bmp.recycle();
        }

        try {
            ExifInterface exif = new ExifInterface(file.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                    scaledBitmap.getHeight(), m, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return scaledBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int
            reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }


    public void takePictureFromCamera(MediaCallback callback) {
        mMediaType = Media.IMAGE;
        File currentFile = null;
        try {
            currentFile = createImageFile();
            if (currentFile != null && currentFile.exists()) {
                currentFile.delete();
            } else {
                if (currentFile != null) {
                    currentFile.getParentFile().mkdirs();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Intent imageSelectIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (mActivity != null) {
            if (imageSelectIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID +
                        ".provider", currentFile);
                imageSelectIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Grant URI permission START
                // Enableing the permission at runtime
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageSelectIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = mActivity.getPackageManager()
                            .queryIntentActivities(imageSelectIntent, PackageManager
                                    .MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        mActivity.grantUriPermission(packageName, photoURI, Intent
                                .FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                }
                currentCodeToSend = currentCodeToSend + 1;
                callback.setMedia(mMediaType);
                callback.setFile(currentFile);
                mediaHolderList.put(currentCodeToSend, callback);
                mActivity.startActivityForResult(Intent.createChooser(imageSelectIntent, "Select " +
                        "Picture"), currentCodeToSend);
            }
        } else if (mFragment != null && mFragment.getContext() != null && mFragment.getContext()
                .getPackageManager() != null) {
            if (imageSelectIntent.resolveActivity(mFragment.getContext().getPackageManager()) !=
                    null) {
                Uri photoURI = FileProvider.getUriForFile(mFragment.getContext(), BuildConfig
                        .APPLICATION_ID + ".provider", currentFile);
                imageSelectIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Grant URI permission START
                // Enableing the permission at runtime
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageSelectIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = mFragment.getContext().getPackageManager()
                            .queryIntentActivities(imageSelectIntent, PackageManager
                                    .MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        mFragment.getContext().grantUriPermission(packageName, photoURI, Intent
                                .FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                }
                currentCodeToSend = currentCodeToSend + 1;
                callback.setMedia(mMediaType);
                callback.setFile(currentFile);
                mediaHolderList.put(currentCodeToSend, callback);
                mFragment.startActivityForResult(Intent.createChooser(imageSelectIntent, "Select " +
                        "Picture"), currentCodeToSend);
            }
        }
    }

    public void takePictureFromCamera(MediaCallback callback, boolean isCompress) {
        takePictureFromCamera(callback);
    }

    public void takePictureFromGallery(MediaCallback callback, boolean isCompress) {
        mMediaType = Media.IMAGE;
        Intent imageSelectIntent = new Intent();
        imageSelectIntent.setAction(Intent.ACTION_PICK);
        imageSelectIntent.setType("image/*");
        currentCodeToSend = currentCodeToSend + 1;
        callback.setMedia(mMediaType);
        mediaHolderList.put(currentCodeToSend, callback);
        if (mActivity != null)
            mActivity.startActivityForResult(Intent.createChooser(imageSelectIntent, "Select " +
                    "Picture"), currentCodeToSend);
        else if (mFragment != null)
            mFragment.startActivityForResult(Intent.createChooser(imageSelectIntent, "Select " +
                    "Picture"), currentCodeToSend);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (mediaHolderList.containsKey(requestCode)) {
                MediaCallback mediaHolder = mediaHolderList.get(requestCode);
                if (mediaHolder != null) {
                    if (mediaHolder.getFile() == null) {
                        // means file not have so get from intent
                        try {
                            mediaHolder.setFile(createImageFile());
                            if (mediaHolder.getFile().exists()) {
                                mediaHolder.getFile().delete();
                            } else {
                                mediaHolder.getFile().getParentFile().mkdirs();
                            }
                            InputStream inputStream = null;
                            if (mActivity != null) {
                                inputStream = mActivity.getContentResolver().openInputStream(data
                                        .getData());
                            } else if (mFragment != null) {
                                inputStream = mFragment.getActivity().getContentResolver()
                                        .openInputStream(data.getData());
                            }
                            if (inputStream != null) {
                                FileOutputStream fileOutputStream = new FileOutputStream
                                        (mediaHolder.getFile());
                                copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            }
                        } catch (Exception e) {
                            mediaHolder.onResult(false, null, mediaHolder.getMediaType());
                        }
                    }
                    mediaHolder.setFile(compressCameraImage(mediaHolder.getFile()));
                    mediaHolder.onResult(true, mediaHolder.getFile(), mediaHolder.getMediaType());
                    mediaHolderList.remove(requestCode);
                }
            }
        }
    }

    private File compressCameraImage(File file) {
        try {
            if (file == null || file.getPath().isEmpty())
                return file;
            Bitmap imgBitmap = getBitmapFromFile(file);
            if (imgBitmap == null)
                return file;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            FileOutputStream fo;
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return file;
    }

    /**
     * Creating the file with folder name coffeeapp
     *
     * @return
     */
    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss"/*, Locale.US*/).format(new Date
                ());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        if (mActivity != null) {
            return new File(new File(mActivity.getFilesDir(), "coffeeapp"), imageFileName);
        } else if (mFragment != null && mFragment.getContext() != null) {
            return new File(new File(mFragment.getContext().getFilesDir(), "coffeeapp"),
                    imageFileName);
        }
        return null;
    }

    public enum Media {
        IMAGE, VIDEO
    }
}