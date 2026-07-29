async function test() {
  try {
    const res = await fetch("http://localhost:8080/api/music/token");
    console.log("STATUS:", res.status);
    console.log("TEXT:", await res.text());
  } catch(e) {
    console.error(e);
  }
}
test();
