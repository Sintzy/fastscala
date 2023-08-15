package com.fastscala.templates.bootstrap5.examples

import com.fastscala.core.{FSContext, FSSession}
import com.fastscala.server.{MainRouterHandlerHelper, Response}
import com.fastscala.templates.bootstrap5.examples.bootstrap.BootstrapTypographyPage
import com.fastscala.utils.{IdGen, RenderableWithFSContext}
import jakarta.servlet.http.HttpServletRequest

import scala.xml.NodeSeq

object MainMenu extends Menu(
  MenuSection("Bootstrap")(
    SimpleMenuItem("Basics", "/bootstrap")
    , SimpleMenuItem("Buttons", "/bootstrap/buttons")
    , new RoutingMenuItem("bootstrap", "typography")("Typography", new BootstrapTypographyPage())
  ),
  MenuSection("Tables")(
    SimpleMenuItem("Simple", "/simple_tables")
    , SimpleMenuItem("Sortable", "/sortable_tables")
    , SimpleMenuItem("Paginated", "/paginated_tables")
    , SimpleMenuItem("Selectable Rows", "/selectable_rows_tables")
    , SimpleMenuItem("Selectable Columns", "/tables_sel_cols")
  ),
  MenuSection("Forms")(
    SimpleMenuItem("Simple", "/simple_form")
  ),
  MenuSection("Modals")(
    SimpleMenuItem("Simple", "/simple_modal")
  ),
  MenuSection("chart.js")(
    SimpleMenuItem("Simple", "/chartjs/simple")
  ),
  MenuSection("Other")(
    SimpleMenuItem("File Upload", "/file_upload")
    , SimpleMenuItem("Anonymous Page", "/anon_page")
    , SimpleMenuItem("File Download", "/file_download")
    , SimpleMenuItem("Server Side Push", "/server_side_push")
  ),
)

case class Menu(sections: MenuSection*) {
  def render()(implicit fsc: FSContext): NodeSeq = {
    <div class="position-sticky p-3 sidebar-sticky">
      <ul class="list-unstyled ps-0">
        {sections.map(_.render())}
      </ul>
    </div>
  }

  def serve()(implicit req: HttpServletRequest, session: FSSession): Option[RenderableWithFSContext] =
    sections.map(_.serve()).find(_.isDefined).flatten
}

case class MenuSection(name: String)(items: MenuItem*) {
  def render()(implicit fsc: FSContext): NodeSeq = {
    val isOpen = items.exists(_.href == fsc.page.req.getRequestURI)
    val id = IdGen.id
    <li class="mb-1">
      <button class={"text-white btn bi btn-toggle d-inline-flex align-items-center rounded border-0" + (if (isOpen) "" else " collapsed")} data-bs-toggle="collapse" data-bs-target={s"#$id"} aria-expanded={isOpen.toString}>
        {name}
      </button>
      <div class={"collapse" + (if (isOpen) " show" else "")} id={id}>
        <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
          {items.map(_.render())}
        </ul>
      </div>
    </li>
  }

  def serve()(implicit req: HttpServletRequest, session: FSSession): Option[RenderableWithFSContext] =
    items.map(_.serve()).find(_.isDefined).flatten
}

trait MenuItem {
  def name: String

  def href: String

  def render()(implicit fsc: FSContext): NodeSeq =
    <li><a href={href} class="text-white d-inline-flex text-decoration-none rounded">{name}</a></li>

  def serve()(implicit req: HttpServletRequest, session: FSSession): Option[RenderableWithFSContext]
}

case class SimpleMenuItem(name: String, href: String) extends MenuItem {
  def serve()(implicit req: HttpServletRequest, session: FSSession): Option[RenderableWithFSContext] = None
}

class RoutingMenuItem(matching: String*)(val name: String, page: => RenderableWithFSContext) extends MenuItem {
  override def href: String = matching.mkString("/", "/", "")

  import com.fastscala.server.RouterHandlerHelper._

  def serve()(implicit req: HttpServletRequest, session: FSSession): Option[RenderableWithFSContext] = Some(req).collect {
    case Get(path@_*) if path == matching => page
  }

}