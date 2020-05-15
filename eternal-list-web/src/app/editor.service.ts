// @ts-nocheck
import {Injectable} from '@angular/core';
import 'ace-builds/src-noconflict/ext-searchbox';
import 'ace-builds/src-noconflict/ext-keybinding_menu';
import 'ace-builds/src-noconflict/ext-statusbar';
import 'ace-builds/src-noconflict/theme-gruvbox';

@Injectable({
  providedIn: 'root'
})
export class EditorService {
  private _editor;

  constructor() {
  }

  get editor() {
    return this._editor;
  }

  public init(editorElement) {
    this._editor = editorElement;
    this._editor.getEditor().setOptions({
      enableBasicAutocompletion: true,
      cursorStyle: 'smooth',
      showLineNumbers: true,
      showPrintMargin: false
    });
    ace.config.loadModule('ace/ext/keybinding_menu', module => {
      module.init(this._editor.getEditor());
    });
    this._editor.getEditor().commands.addCommand({
      name: 'showKeyboardShortcuts',
      bindKey: {win: 'Ctrl-Alt-h', mac: 'Command-Alt-h'},
      exec(editor) {
        // tslint:disable-next-line:only-arrow-functions
        ace.config.loadModule('ace/ext/keybinding_menu', module => {
          editor.showKeyboardShortcuts();
        });
      }
    });
    this._editor.setMode("ace/mode/text");
    // var hyper = hyperace.create(this.editor, 'results');
    // serach.nativeElement.addEventListener('change', function () {
    //   editor.getSession().setMode({"path": "ace/mode/" + this.value});
    // });
  }

  public showShortCuts() {
    this._editor.getEditor().showKeyboardShortcuts();
  }
}
