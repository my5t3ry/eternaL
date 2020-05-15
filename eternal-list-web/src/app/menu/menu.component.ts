import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {EditorService} from "../editor.service";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  constructor(private keycloakService: KeycloakService, private editorService: EditorService) {
  }

  ngOnInit(): void {
  }

  logout() {
    this.keycloakService.logout();
  }

  info() {
    this.editorService.showShortCuts();
  }
}
