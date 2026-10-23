const renderMarkdownImage = (tokens, idx, _options, _env, _self) => {
  const token = tokens[idx];
  return `<ClientOnly><Img src="${token.attrGet("src")}" alt="${token.attrGet(
    "alt",
  )}" /></ClientOnly>`;
};

export default renderMarkdownImage;