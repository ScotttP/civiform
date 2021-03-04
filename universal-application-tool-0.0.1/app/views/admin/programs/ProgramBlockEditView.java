package views.admin.programs;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.form;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.li;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static j2html.TagCreator.ul;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.Tag;
import play.mvc.Http.Request;
import play.twirl.api.Content;
import services.program.BlockDefinition;
import services.program.ProgramDefinition;
import services.question.QuestionDefinition;
import views.BaseHtmlView;
import views.Styles;
import views.StyleUtils;
import views.admin.AdminLayout;

public class ProgramBlockEditView extends BaseHtmlView {

  private final AdminLayout layout;
  
  private final String TEXT_COLOR = Styles.TEXT_GRAY_300;
  private final String BG_COLOR = Styles.BG_GRAY_800;
  // private final String BG_SECONDARY_COLOR = Styles.BG_GRAY_700;
  private final String BG_TERTIARY_COLOR = Styles.BG_GRAY_600;  
  /*
  private final String SELECTED_STYLING = BG_TERTIARY_COLOR;
  */
  private final String BLOCK_HOVER_STYLING = StyleUtils.hover(Styles.BG_GRAY_500);
  
  @Inject
  public ProgramBlockEditView(AdminLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render(
      Request request,
      ProgramDefinition program,
      BlockDefinition block,
      ImmutableList<QuestionDefinition> questions) {
    Tag csrfTag = makeCsrfTokenInputTag(request);

    boolean useOldVersion = false;
    if (useOldVersion) {
      return layout.render(
          title(program),
          topButtons(program),
          div()
              .withClasses(Styles.FLEX)
              .with(blockOrderPanel(program, block))
              .with(blockEditPanel(csrfTag, program, block))
              .with(questionBankPanel(questions)));
    }

      return layout.renderBody(
        div().withClasses(BG_COLOR, TEXT_COLOR)
      .with(
        renderHeaderSection(program.name()),
        renderMainContent(csrfTag, program, block, questions)
      ));    
  }

  private Tag renderHeaderSection(String appTitle) {
    // header (back to "All Applications", program title, logged in user)
    String link = controllers.admin.routes.AdminProgramController.index().url();
    Tag allAppsLink = a("← All Applications").withHref(link)
        .withClasses("inline-block text-left w-1/5 hover:text-white transition-all transform hover:scale-105");
    Tag titleTag = p(span(appTitle)).withClasses("flex-grow text-center text-white");
    Tag userProfileTag = p("User").withClasses("inline-block text-right w-1/5");
    return div().withClasses("bg-gray-700 px-8 py-4 font-light flex absolute inset-x-0 top-0 text-md")
      .with(allAppsLink, titleTag, userProfileTag );
  }

  private Tag renderMainContent(
    Tag csrfTag, ProgramDefinition program, BlockDefinition block, ImmutableList<QuestionDefinition> questions) {
    // body (blocks, current block, question bank) 
    return div().withClasses("flex p-relative h-screen pt-8")
    .with(renderSidebar(program, block), renderMain(csrfTag, program, block), renderQuestionBank(questions)); 
  }

  private Tag renderSidebar(ProgramDefinition program, BlockDefinition focusedBlock) {
    return div(
      blockOrderPanel(program, focusedBlock),
      renderAddBlockTag(program)
    ).withClass("inline-block shadow-lg border-r border-gray-500 pt-8").withStyle("width: 375px;");
  }

  private Tag renderMain(Tag csrfTag, ProgramDefinition program, BlockDefinition focusedBlock) {
    return div(
      blockEditPanel(csrfTag, program, focusedBlock)
    ).withClass("inline-block w-1/2  m-0 pt-8").withStyle("width: calc(100% - 750px);");
  }
  
  private Tag renderQuestionBank(ImmutableList<QuestionDefinition> questions) {
    return div(questionBankPanel(questions))
      .withClass("inline-block w-1/3 m-0 pt-12 pb-12 pr-4")
      .withStyle("width: 375px;");
  }


  private Tag title(ProgramDefinition program) {
    return h1(program.name() + " Questions");
  }

  private ContainerTag topButtons(ProgramDefinition program) {
    return renderAddBlockTag(program);    
  }

  private ContainerTag renderAddBlockTag(ProgramDefinition program) {
    String addBlockUrl =
        controllers.admin.routes.AdminProgramBlocksController.create(program.id()).url();

    return div(a("Add Block").withHref(addBlockUrl));
  }

  private ContainerTag blockOrderPanel(ProgramDefinition program, BlockDefinition focusedBlock) {
    ContainerTag ret = div();
    for (BlockDefinition block : program.blockDefinitions()) {
      String editBlockLink =
          controllers.admin.routes.AdminProgramBlocksController.edit(program.id(), block.id())
              .url();
      ContainerTag linkContent = div().withClasses(Styles.ML_12).with(
        p(block.name()).withClasses("text-base font-medium text-primaryText"),
        p(block.description() + "\u00a0").withClasses("mt-1 text-sm text-secondaryText")
      );
      ContainerTag link = a(linkContent).withHref(editBlockLink);
      String selectedStyling = block.hasSameId(focusedBlock) ? BG_TERTIARY_COLOR : "";
      link.withClasses(Styles.BLOCK, Styles.P_2, BLOCK_HOVER_STYLING, selectedStyling);

      ret.with(link);
    }

    return ret;
  }

  private ContainerTag blockEditPanel(
      Tag csrfTag, ProgramDefinition program, BlockDefinition block) {
    return div()
        .withClass(Styles.FLEX_AUTO)
        .with(blockEditPanelTop(csrfTag, program, block))
        .with(
            div(
                form(
                        csrfTag,
                        div(textFieldWithValue("name", "Block Name", block.name())),
                        div(
                            textFieldWithValue(
                                "description", "Block Description", block.description())),
                        submitButton("Update Block"))
                    .withMethod("post")
                    .withAction(
                        controllers.admin.routes.AdminProgramBlocksController.update(
                                program.id(), block.id())
                            .url())));
  }

  private ContainerTag blockEditPanelTop(
      Tag csrfTag, ProgramDefinition program, BlockDefinition block) {
    String deleteBlockLink =
        controllers.admin.routes.AdminProgramBlocksController.destroy(program.id(), block.id())
            .url();
    ContainerTag deleteButton =
        form(csrfTag, submitButton("Delete Block")).withMethod("post").withAction(deleteBlockLink);
    return div().withClass(Styles.FLEX).with(h1(block.name())).with(deleteButton);
  }

  private ContainerTag questionBankPanel(ImmutableList<QuestionDefinition> questionDefinitions) {
    ContainerTag ret = div().withClasses("inline-block w-1/3 m-0 pb-6 pr-4").withStyle("width: 375px;");
    ContainerTag innerDiv = div().withClasses("rounded-lg bg-gray-600 shadow-lg ring-2 ring-black ring-opacity-5 overflow-hidden pt-1");
    ret.with(innerDiv);
    ContainerTag contentDiv = div().withClasses("relative grid gap-6 bg-secondary px-5 py-6");
    innerDiv.with(contentDiv);

    ContainerTag headerDiv = h1("Question bank").withClasses("mx-1 px-3 -mb-1 text-xl");
    contentDiv.with(headerDiv);

    ContainerTag filterDiv = div().withClasses("relative text-primaryText w-85");
    contentDiv.with(filterDiv);

    questionDefinitions.forEach(
        questionDefinition -> contentDiv.with(li(questionDefinition.getName())));

    return ret;
  }

  //     <!-- Filter questions -->
  //     <div class="relative text-primaryText w-85">
  //       <div class="absolute ml-4 mt-3 mr-4">
  //         <svg class="h-4 w-4 fill-current" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" id="Capa_1" x="0px" y="0px" viewBox="0 0 56.966 56.966" style="enable-background:new 0 0 56.966 56.966;" xml:space="preserve" width="512px" height="512px">
  //           <path d="M55.146,51.887L41.588,37.786c3.486-4.144,5.396-9.358,5.396-14.786c0-12.682-10.318-23-23-23s-23,10.318-23,23  s10.318,23,23,23c4.761,0,9.298-1.436,13.177-4.162l13.661,14.208c0.571,0.593,1.339,0.92,2.162,0.92  c0.779,0,1.518-0.297,2.079-0.837C56.255,54.982,56.293,53.08,55.146,51.887z M23.984,6c9.374,0,17,7.626,17,17s-7.626,17-17,17  s-17-7.626-17-17S14.61,6,23.984,6z"></path>
  //         </svg>
  //       </div>
  //       <input type="questionFilter" name="questionFilter" placeholder="Filter questions" class="h-10 px-10 pr-5 w-full rounded-full text-sm border border-gray-200 focus:outline-none shadow bg-grey-500 text-secondaryText" />
  //     </div>

  //     <!-- Address question -->
  //     <div class="-m-3 p-3 flex items-start rounded-lg hover:bg-gray-200 hover:text-gray-800 transition-all transform hover:scale-105">
  //       <svg class="flex-shrink-0 h-12 w-6 text-primaryText" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
  //         <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z" />
  //       </svg>
  //       <div class="ml-4">
  //         <p class="text-base font-medium text-primaryText">Your address</p>
  //         <p class="mt-1 text-sm text-secondaryText">Residential address in the city of Seattle</p>
  //       </div>
  //     </div>
}
