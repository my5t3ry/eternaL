// @ts-nocheck
import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {EditorService} from '../editor.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements AfterViewInit, OnInit {

  @ViewChild('editor') editor;
  @ViewChild('status') status;
  text = '';
  value: any;

  constructor(private http: HttpClient, private editorService: EditorService) {
    // @ts-ignore
  }

  ngAfterViewInit() {
    this.editorService.init(this.editor);
  }

  onChange($event: any) {
    console.log($event);
    this.http
      .post('api/editor', $event, {})
      .toPromise()
      .then((res: any) => {
          console.log(this);
        },
        err => {
          console.error(err);
        }
      );
  }

  ngOnInit(): void {
    this.http.get('api/editor/default').subscribe((data) => {
      if (data != null) {
        // @ts-ignore
        this.text = data.value;
      }
    });
  }
}
