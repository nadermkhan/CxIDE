package cn.rbc.termuc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.Toast;
import java.io.File;
import cn.rbc.codeeditor.view.*;
import cn.rbc.codeeditor.util.*;
import cn.rbc.codeeditor.view.autocomplete.*;
import cn.rbc.codeeditor.lang.*;

public class TextEditor extends FreeScrollingTextField{
   // private Document _inputtingDoc;
   // private boolean _isWordWrap;
    private Context mContext;
    private String _lastSelectFile;
    private int _index;
	//private int mSize = 14;
    /*
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case ReadThread.MSG_READ_OK:
                    setText(msg.obj.toString());
                    break;
                case ReadThread.MSG_READ_FAIL:
                    showToast("打开失败");
                    break;
                case WriteThread.MSG_WRITE_OK:
                    showToast("保存成功");
                    break;
                case WriteThread.MSG_WRITE_FAIL:
                    showToast("保存失败");
                    break;
            }
        }
    };*/

    public TextEditor(Context context) {
        super(context);
        mContext = context;
        init(mContext);
    }

    public TextEditor(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init(mContext);
    }

    private void init(Context mContext) {
        //setVerticalScrollBarEnabled(true);
        //setTypeface(Typeface.MONOSPACE);
	    Typeface customFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/jbm.ttf");

    // Set the custom font as typeface
    setTypeface(customFont);

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        //设置字体大小
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16.f, dm);
        setTextSize((int) size);
        setShowLineNumbers(true);
        setHighlightCurrentRow(true);
        //setWordWrap(true);
        setAutoComplete(false);
        setAutoIndent(true);
        setUseGboard(true);
        //setLanguage(LanguageC.getInstance());
        setNavigationMethod(new YoyoNavigationMethod(this));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (_index != 0 && right > 0) {
            moveCaret(_index);
            _index = 0;
        }
    }

    public static void setLanguage(Language language){
        AutoCompletePanel.setLanguage(language);
        Tokenizer.setLanguage(language);
    }

    public String getSelectedText() {
        return hDoc.subSequence(getSelectionStart(), getSelectionEnd() - getSelectionStart()).toString();
    }

    public void gotoLine(int line) {
        if (line > hDoc.getRowCount()) {
            line = hDoc.getRowCount();
        }
        int i = getText().getLineOffset(line - 1);
        setSelection(i);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        final int filteredMetaState = event.getMetaState() & ~KeyEvent.META_CTRL_MASK;
        if (KeyEvent.metaStateHasNoModifiers(filteredMetaState)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_A:
                    selectAll();
                    return true;
                case KeyEvent.KEYCODE_X:
                    cut();
                    return true;
                case KeyEvent.KEYCODE_C:
                    copy();
                    return true;
                case KeyEvent.KEYCODE_V:
                    paste();
                    return true;
				case KeyEvent.KEYCODE_EQUALS:
					setTextSize((int)(getTextSize()+HelperUtils.getDpi(mContext)));
					return true;
				case KeyEvent.KEYCODE_MINUS:
					setTextSize((int)(getTextSize()-HelperUtils.getDpi(mContext)));
					return true;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }

    public void setText(CharSequence c) {
        Document doc = new Document(this);
        doc.setWordWrap(isWordWrap());
        doc.setText(c);
        setDocument(doc);
    }

	public AutoCompletePanel getAutoCompletePanel() {
		return mAutoCompletePanel;
	}

    public File getOpenedFile() {
        if (_lastSelectFile != null)
            return new File(_lastSelectFile);

        return null;
    }

    public void setOpenedFile(String file) {
        _lastSelectFile = file;
    }

    public void replaceAll(CharSequence c) {
        replaceText(0, getLength() - 1, c.toString());
    }

    public void setSelection(int index) {
        selectText(false);
        if (!hasLayout())
            moveCaret(index);
        else
            _index = index;
    }

    public void undo() {
        int newPosition = hDoc.undo();

        if (newPosition >= 0) {
            //TODO editor.setEdited(false);
            // if reached original condition of file
            setEdited(true);
            mCtrlr.determineSpans();
			//tc
            selectText(false);
            moveCaret(newPosition);
        }
    }

    public void redo() {
        int newPosition = hDoc.redo();

        if (newPosition >= 0) {
            setEdited(true);
            mCtrlr.determineSpans();
			//tc
            selectText(false);
            moveCaret(newPosition);
        }
    }
/*
    public void open(String filename) {
        _lastSelectFile = filename;

        File inputFile = new File(filename);
        _inputtingDoc = new Document(this);
        _inputtingDoc.setWordWrap(this.isWordWrap());
        ReadThread readThread = new ReadThread(inputFile.getAbsolutePath(), handler);
        readThread.start();
    }

    /**
     * 保存文件
     * * @param file
     */
/*
    public void save(String file) {
        WriteThread writeThread = new WriteThread(getText().toString(), file, handler);
        writeThread.start();
    }
*
    private void showToast(CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }*/
}

