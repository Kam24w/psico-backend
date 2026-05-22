const testAuth = async () => {
    try {
        console.log("Registering user...");
        const regRes = await fetch("http://localhost:8080/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: "test", email: "test2@test.com", password: "password" })
        });
        const regText = await regRes.text();
        console.log("Reg status:", regRes.status, regText);

        console.log("Logging in...");
        const loginRes = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: "test2@test.com", password: "password" })
        });
        const loginText = await loginRes.text();
        console.log("Login status:", loginRes.status, loginText);
    } catch (e) {
        console.error("Error:", e);
    }
};

testAuth();
