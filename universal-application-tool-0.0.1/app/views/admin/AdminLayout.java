package views.admin;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.head;
import static j2html.TagCreator.main;
import static j2html.TagCreator.nav;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import javax.inject.Inject;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.StyleUtils;
import views.Styles;
import views.ViewUtils;

public class AdminLayout extends BaseHtmlLayout {

  @Inject
  public AdminLayout(ViewUtils viewUtils) {
    super(viewUtils);
  }

  /** Renders mainDomContents within the main tag, in the context of the admin layout. */
  public Content render(DomContent... mainDomContents) {
    ContainerTag subWrapper =
        div(renderAdminHeader(), main(mainDomContents))
            .withClasses("flex flex-col flex-1 h-full overflow-hidden");
    ContainerTag mainWrapper = div(subWrapper).withClasses("flex h-screen overflow-y-hidden");
    return htmlContent(head(tailwindStyles()), body(mainWrapper));
  }

  public ContainerTag renderAdminHeader() {
    String questionTitle = "Questions";
    String questionLink = controllers.admin.routes.QuestionController.index("table").url();
    String programTitle = "Programs";
    String programLink = controllers.admin.routes.AdminProgramController.index().url();
    String logoutTitle = "Logout";
    String logoutLink = org.pac4j.play.routes.LogoutController.logout().url();

    ContainerTag adminHeader =
        nav()
            .withClasses(
                Styles.BG_GRAY_800, Styles.TEXT_GRAY_200, Styles.H_12, Styles.PX_4, Styles.PY_3)
            .with(
                a(questionTitle)
                    .withClasses(Styles.PX_3, StyleUtils.hover(Styles.TEXT_WHITE))
                    .withHref(questionLink),
                a(programTitle)
                    .withClasses(Styles.PX_3, StyleUtils.hover(Styles.TEXT_WHITE))
                    .withHref(programLink),
                a(logoutTitle)
                    .withClasses(
                        Styles.PX_3, StyleUtils.hover(Styles.TEXT_WHITE), Styles.FLOAT_RIGHT)
                    .withHref(logoutLink));
    return adminHeader;
  }
}
