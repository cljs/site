# We just want to make some ClojureScript-specific things to the markdown processor:
# - CLJS-NNNN -> jira issue links
# - ...
#
# guidance from: http://stackoverflow.com/a/24760839/142317

require 'jekyll'
require 'redcarpet'

def preprocess_for_cljs(content)
  content
end

# THIS IS PASTED from the jekyll repo v2.5.3:
# <https://github.com/jekyll/jekyll/blob/v2.5.3/lib/jekyll/converters/markdown/redcarpet_parser.rb#L94-L99>
# 
# If we need to update Jekyll, check the version in Gemfile.lock,
# then repaste the "convert" method here, then modify.
#
# The only modification is the creation and usage of 'extended_render' in place of '@renderer'
class Jekyll::Converters::Markdown::RedcarpetCLJS < Jekyll::Converters::Markdown::RedcarpetParser
  def convert(content)
    @redcarpet_extensions[:fenced_code_blocks] = !@redcarpet_extensions[:no_fenced_code_blocks]
    @renderer.send :include, Redcarpet::Render::SmartyPants if @redcarpet_extensions[:smart]

    extended_renderer = Class.new(@renderer) do
      def preprocess(content)
        preprocess_for_cljs(content)
      end
    end

    markdown = Redcarpet::Markdown.new(extended_renderer.new(@redcarpet_extensions), @redcarpet_extensions)
    markdown.render(content)
  end
end

